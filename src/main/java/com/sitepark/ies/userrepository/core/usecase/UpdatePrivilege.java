package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.exception.UpdatePrivilegeFailedException;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdatePrivilege {

  private static final Logger LOGGER = LogManager.getLogger();

  private final AssignPrivilegesToRoles assignPrivilegesToRolesUseCase;
  private final PrivilegeRepository repository;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final PatchService<Privilege> patchService;
  private final Clock clock;

  @Inject
  UpdatePrivilege(
      AssignPrivilegesToRoles assignPrivilegesToRolesUseCase,
      PrivilegeRepository repository,
      AccessControl accessControl,
      AuditLogService auditLogService,
      PatchServiceFactory patchServiceFactory,
      Clock clock) {
    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
    this.repository = repository;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.patchService = patchServiceFactory.createPatchService(Privilege.class);
    this.clock = clock;
  }

  public String updatePrivilege(UpdatePrivilegeRequest request) {

    this.validatePrivilege(request.privilege());

    this.checkAccessControl(request.privilege(), request.roleIds());

    Privilege newPrivilege = this.toPrivilegeWithId(request.privilege());

    this.validateAnchor(newPrivilege);
    this.repository.validatePermission(newPrivilege.permission());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update privilege: {}", request.privilege());
    }

    Privilege oldPrivilege =
        this.repository
            .get(newPrivilege.id())
            .orElseThrow(
                () ->
                    new UpdatePrivilegeFailedException(
                        "No privilege with ID " + newPrivilege.id() + " found."))
            .toBuilder()
            .clearRoleIds()
            .build();

    PatchDocument patch = this.patchService.createPatch(oldPrivilege, newPrivilege);

    if (patch.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip update, privilege with ID {} is unchanged.", newPrivilege.id());
      }
    } else {
      this.repository.update(newPrivilege);
      PatchDocument revertPatch = this.patchService.createPatch(newPrivilege, oldPrivilege);
      this.auditLogService.createAuditLog(
          this.buildCreateAuditLogRequest(
              newPrivilege.id(), newPrivilege.name(), patch, revertPatch, request.auditParentId()));
    }

    if (!request.roleIds().isEmpty()) {
      this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
          AssignPrivilegesToRolesRequest.builder()
              .roleIds(request.roleIds())
              .privilegeId(newPrivilege.id())
              .build());
    }

    return newPrivilege.id();
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.name() == null || privilege.name().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.permission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }
  }

  private void checkAccessControl(Privilege privilege, List<String> roleIds) {
    if (!this.accessControl.isPrivilegeWritable()) {
      throw new AccessDeniedException("Not allowed to update privilege " + privilege);
    }

    if (!roleIds.isEmpty() && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to add privilege " + privilege + " -> " + roleIds);
    }
  }

  private Privilege toPrivilegeWithId(Privilege privilege) {
    if (privilege.id() == null) {
      if (privilege.anchor() != null) {
        String id =
            this.repository
                .resolveAnchor(privilege.anchor())
                .orElseThrow(() -> new AnchorNotFoundException(privilege.anchor()));
        return privilege.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException(
          "Neither id nor anchor is specified to update the privilege.");
    }
    return privilege;
  }

  private void validateAnchor(Privilege privilege) {
    if (privilege.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(privilege.anchor());
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(privilege.id())) {
              throw new AnchorAlreadyExistsException(privilege.anchor(), owner);
            }
          });
    }
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      String entityId,
      String entityName,
      PatchDocument patch,
      PatchDocument revertPatch,
      String parentId) {

    return new CreateAuditLogRequest(
        AuditLogEntityType.PRIVILEGE.name(),
        entityId,
        entityName,
        AuditLogAction.UPDATE.name(),
        revertPatch.toJson(),
        patch.toJson(),
        Instant.now(this.clock),
        parentId);
  }
}
