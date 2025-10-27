package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.RoleSnapshot;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreateRoleUseCase {

  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  CreateRoleUseCase(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase,
      AccessControl accessControl,
      AuditLogService auditLogService,
      Clock clock) {
    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public String createRole(CreateRoleRequest request) {

    this.validateRole(request.role());

    this.checkAccessControl(request.role());

    this.validateAnchor(request.role());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create role: {}", request.role());
    }

    String id = this.roleRepository.create(request.role());

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(
            new RoleSnapshot(request.role().toBuilder().id(id).build(), List.of(), privilegeIds),
            request.auditParentId());
    this.auditLogService.createAuditLog(createAuditLogRequest);

    if (!privilegeIds.isEmpty()) {
      this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
          AssignPrivilegesToRolesRequest.builder()
              .roleIdentifiers(b -> b.id(id))
              .privilegeIdentifiers(b -> b.ids(privilegeIds))
              .build());
    }

    return id;
  }

  private void validateRole(Role role) {
    if (role.id() != null) {
      throw new IllegalArgumentException("The ID of the role must not be set when creating.");
    }
    if (role.name() == null || role.name().isBlank()) {
      throw new IllegalArgumentException("The name of the role must not be null or empty.");
    }
  }

  private void checkAccessControl(Role role) {
    if (!this.accessControl.isRoleCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.roleRepository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(role.anchor(), owner);
          });
    }
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      RoleSnapshot snapshot, String auditLogParentId) {

    String forwardData;
    try {
      forwardData = this.auditLogService.serialize(snapshot);
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.ROLE.name(), snapshot.role().id(), snapshot.role().name(), e);
    }

    return new CreateAuditLogRequest(
        AuditLogEntityType.ROLE.name(),
        snapshot.role().id(),
        snapshot.role().name(),
        AuditLogAction.CREATE.name(),
        null,
        forwardData,
        Instant.now(this.clock),
        auditLogParentId);
  }
}
