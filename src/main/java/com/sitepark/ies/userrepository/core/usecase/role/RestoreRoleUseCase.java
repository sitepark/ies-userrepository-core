package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.RoleSnapshot;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RestoreRoleUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  @Inject
  RestoreRoleUseCase(
      RoleRepository repository,
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

  public void restoreRole(RestoreRoleRequest request) {

    Role role = request.data().role();
    List<String> userIds = request.data().userIds();
    List<String> privilegeIds = request.data().privilegesIds();
    String auditBatchId = request.auditParentId();

    this.validateRole(role);

    this.checkAccessControl(role, userIds);

    if (this.repository.get(role.id()).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, role with ID {} already exists.", role.id());
      }
      return;
    }

    this.validateAnchor(role);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("restore role: {}", role);
    }

    Instant now = Instant.now(this.clock);

    RoleSnapshot restoreData = new RoleSnapshot(role, userIds, privilegeIds);

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(restoreData, now, auditBatchId);

    this.repository.restore(role);
    if (!userIds.isEmpty()) {
      String roleId = role.id();
      assert roleId != null : "role.id() was validated in validateUser()";
      this.roleAssigner.assignRolesToUsers(userIds, List.of(roleId));
    }
    if (!privilegeIds.isEmpty()) {
      String roleId = role.id();
      assert roleId != null : "role.id() was validated in validateUser()";
      this.roleAssigner.assignPrivilegesToRoles(List.of(roleId), privilegeIds);
    }

    this.auditLogService.createAuditLog(createAuditLogRequest);
  }

  private void validateRole(Role role) {
    if (role.id() == null || role.id().isBlank()) {
      throw new IllegalArgumentException("The id of the role must not be null or empty.");
    }
    if (role.name() == null || role.name().isBlank()) {
      throw new IllegalArgumentException("The name of the role must not be null or empty.");
    }
  }

  private void checkAccessControl(Role role, List<String> userIds) {
    if (!this.accessControl.isRoleCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }

    if (userIds != null && !userIds.isEmpty() && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update user to create role " + role + " -> " + userIds);
    }
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(role.anchor(), owner);
          });
    }
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      RoleSnapshot data, Instant now, String auditLogParentId) {
    String json = serializeRole(data);
    return new CreateAuditLogRequest(
        AuditLogEntityType.ROLE.name(),
        data.role().id(),
        data.role().name(),
        AuditLogAction.RESTORE.name(),
        null,
        json,
        now,
        auditLogParentId);
  }

  private String serializeRole(RoleSnapshot data) {
    try {
      return this.objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.PRIVILEGE.name(), data.role().id(), data.role().name(), e);
    }
  }
}
