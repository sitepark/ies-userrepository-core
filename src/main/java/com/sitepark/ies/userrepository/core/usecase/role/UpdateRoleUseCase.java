package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.exception.RoleNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdateRoleUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private final RoleRepository repository;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final AuditLogService auditLogService;
  private final PatchService<Role> patchService;
  private final Clock clock;

  @Inject
  UpdateRoleUseCase(
      AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase,
      RoleRepository repository,
      RoleEntityAuthorizationService roleEntityAuthorizationService,
      AuditLogService auditLogService,
      PatchServiceFactory patchServiceFactory,
      Clock clock) {

    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
    this.repository = repository;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
    this.auditLogService = auditLogService;
    this.patchService = patchServiceFactory.createPatchService(Role.class);
    this.clock = clock;
  }

  public String updateRole(UpdateRoleRequest request) {

    this.validateRole(request.role());

    Role newRole;
    if (request.role().id() == null) {
      newRole = this.toRoleWithId(request.role());
    } else {
      this.validateAnchor(request.role());
      newRole = request.role();
    }

    this.checkAuthorization(newRole);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update role: {}", newRole);
    }

    Role oldRole =
        this.repository
            .get(newRole.id())
            .orElseThrow(
                () -> new RoleNotFoundException("No role with ID " + newRole.id() + " found."))
            .toBuilder()
            .build();

    Instant timestamp = Instant.now(this.clock);

    PatchDocument patch = this.patchService.createPatch(oldRole, newRole);

    if (patch.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip update, role with ID {} is unchanged.", newRole.id());
      }
    } else {
      this.repository.update(newRole);
      PatchDocument revertPatch = this.patchService.createPatch(newRole, oldRole);
      this.auditLogService.createAuditLog(
          this.buildCreateAuditLogRequest(
              newRole.id(),
              newRole.name(),
              patch,
              revertPatch,
              request.auditParentId(),
              timestamp));
    }

    if (!request.privilegeIdentifiers().isEmpty()) {
      this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
          AssignPrivilegesToRolesRequest.builder()
              .roleIdentifiers(b -> b.id(newRole.id()))
              .privilegeIdentifiers(b -> b.identifiers(request.privilegeIdentifiers()))
              .build());
    }

    return newRole.id();
  }

  private void validateRole(Role role) {
    assert role.name() != null && !role.name().isBlank();
  }

  private void checkAuthorization(Role role) {
    if (!this.roleEntityAuthorizationService.isWritable(role.id())) {
      throw new AccessDeniedException("Not allowed to update role " + role);
    }
  }

  private Role toRoleWithId(Role role) {
    if (role.id() == null) {
      if (role.anchor() != null) {
        String id =
            this.repository
                .resolveAnchor(role.anchor())
                .orElseThrow(() -> new AnchorNotFoundException(role.anchor()));
        return role.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException("Neither id nor anchor is specified to update the role.");
    }
    return role;
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(role.id())) {
              throw new AnchorAlreadyExistsException(role.anchor(), owner);
            }
          });
    }
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      String entityId,
      String entityName,
      PatchDocument patch,
      PatchDocument revertPatch,
      String parentId,
      Instant timestamp) {

    return new CreateAuditLogRequest(
        AuditLogEntityType.ROLE.name(),
        entityId,
        entityName,
        AuditLogAction.UPDATE.name(),
        revertPatch.toJson(),
        patch.toJson(),
        timestamp,
        parentId);
  }
}
