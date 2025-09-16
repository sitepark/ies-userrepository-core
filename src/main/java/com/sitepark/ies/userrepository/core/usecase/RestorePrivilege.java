package com.sitepark.ies.userrepository.core.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class RestorePrivilege {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  @Inject
  RestorePrivilege(
      PrivilegeRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      AuditLogService auditLogService,
      ObjectMapper objectMapper,
      Clock clock) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  public void restorePrivilege(RestorePrivilegeRequest request) {

    Privilege privilege = request.privilege();
    String[] roleIds = request.roleIds();
    String auditBatchId = request.auditBatchId();

    this.validatePrivilege(privilege);

    this.checkAccessControl(privilege, roleIds);

    this.validateAnchor(privilege);

    this.repository.validatePermission(privilege.permission());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("restore privilege: {}", privilege);
    }

    Instant now = Instant.now(this.clock);

    Privilege privilegeForAuditLog =
        roleIds.length > 0 ? privilege.toBuilder().roleIds(roleIds).build() : privilege;

    CreateAuditLogRequest createAuditLogCommand =
        this.buildCreateAuditLogCommand(privilegeForAuditLog, now, auditBatchId);

    this.repository.restore(privilege);
    if (roleIds.length > 0) {
      String privilegeId = privilege.id();
      assert privilegeId != null : "privilege.id() was validated in validatePrivilege()";
      this.roleAssigner.assignPrivilegesToRoles(Arrays.asList(roleIds), List.of(privilegeId));
    }

    this.auditLogService.createAuditLog(createAuditLogCommand);
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.id() == null || privilege.id().isBlank()) {
      throw new IllegalArgumentException("The id of the privilege must not be null or empty.");
    }
    if (privilege.name() == null || privilege.name().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.permission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }
    validatePrivilegeDoesNotExist(privilege.id());
  }

  private void validatePrivilegeDoesNotExist(String privilegeId) {
    if (this.repository.get(privilegeId).isPresent()) {
      throw new IllegalArgumentException(
          "The privilege with id " + privilegeId + " already exists.");
    }
  }

  @SuppressWarnings("PMD.UseVarargs")
  private void checkAccessControl(Privilege privilege, @Nullable String[] roleIds) {
    if (!this.accessControl.isPrivilegeCreatable()) {
      throw new AccessDeniedException("Not allowed to create privilege " + privilege);
    }

    if (roleIds != null && roleIds.length > 0 && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to create privilege "
              + privilege
              + " -> "
              + Arrays.toString(roleIds));
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

  private CreateAuditLogRequest buildCreateAuditLogCommand(
      Privilege privilege, Instant now, String batchId) {
    String json = serializePrivilege(privilege);
    return new CreateAuditLogRequest(
        AuditLogEntityType.PRIVILEGE.name(),
        privilege.id(),
        AuditLogAction.RESTORE.name(),
        json,
        null,
        now,
        batchId);
  }

  private String serializePrivilege(Privilege privilege) {
    try {
      return this.objectMapper.writeValueAsString(privilege);
    } catch (JsonProcessingException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.PRIVILEGE.name(), privilege.id(), e);
    }
  }
}
