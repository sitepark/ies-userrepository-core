package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.usecase.audit.PrivilegeSnapshot;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreatePrivilege {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  @Inject
  CreatePrivilege(
      PrivilegeRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      AuditLogService auditLogService,
      Clock clock) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public String createPrivilege(CreatePrivilegeRequest request) {

    this.validatePrivilege(request.privilege());

    this.checkAccessControl(request.privilege(), request.roleIds());

    this.validateAnchor(request.privilege());

    this.repository.validatePermission(request.privilege().permission());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create privilege: {}", request.privilege());
    }

    String id = this.repository.create(request.privilege());

    if (!request.roleIds().isEmpty()) {
      this.roleAssigner.assignPrivilegesToRoles(request.roleIds(), List.of(id));
    }

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(
            new PrivilegeSnapshot(
                request.privilege().toBuilder().id(id).build(), request.roleIds()),
            request.auditParentId());
    this.auditLogService.createAuditLog(createAuditLogRequest);

    return id;
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.id() != null) {
      throw new IllegalArgumentException("The ID of the privilege must not be set when creating.");
    }
    if (privilege.name() == null || privilege.name().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.permission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }
  }

  private void checkAccessControl(Privilege privilege, List<String> roleIds) {
    if (!this.accessControl.isPrivilegeCreatable()) {
      throw new AccessDeniedException("Not allowed to create privilege " + privilege);
    }

    if (!roleIds.isEmpty() && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to add privilege " + privilege + " -> " + roleIds);
    }
  }

  private void validateAnchor(Privilege privilege) {
    if (privilege.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(privilege.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(privilege.anchor(), owner);
          });
    }
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      PrivilegeSnapshot snapshot, String auditLogParentId) {

    String forwardData;
    try {
      forwardData = this.auditLogService.serialize(snapshot);
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.PRIVILEGE.name(),
          snapshot.privilege().id(),
          snapshot.privilege().name(),
          e);
    }

    return new CreateAuditLogRequest(
        AuditLogEntityType.PRIVILEGE.name(),
        snapshot.privilege().id(),
        snapshot.privilege().name(),
        AuditLogAction.CREATE.name(),
        null,
        forwardData,
        Instant.now(this.clock),
        auditLogParentId);
  }
}
