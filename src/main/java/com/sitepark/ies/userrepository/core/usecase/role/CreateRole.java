package com.sitepark.ies.userrepository.core.usecase.role;

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
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreateRole {

  private final RoleRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  CreateRole(
      RoleRepository repository,
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

  public String createRole(CreateRoleRequest request) {

    this.validateRole(request.role());

    this.checkAccessControl(request.role(), request.privilegeIds());

    this.validateAnchor(request.role());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create role: {}", request.role());
    }

    String id = this.repository.create(request.role());

    if (!request.privilegeIds().isEmpty()) {
      this.roleAssigner.reassignPrivilegesToRoles(List.of(id), request.privilegeIds());
    }

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(
            new RoleSnapshot(
                request.role().toBuilder().id(id).build(), List.of(), request.privilegeIds()),
            request.auditParentId());
    this.auditLogService.createAuditLog(createAuditLogRequest);

    return id;
  }

  private void validateRole(Role role) {
    assert role.name() != null && !role.name().isBlank();
    if (role.id() != null) {
      throw new IllegalArgumentException("The ID of the privilege must not be set when creating.");
    }
  }

  private void checkAccessControl(Role role, List<String> privilegeIds) {
    if (!this.accessControl.isRoleCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }

    if (!privilegeIds.isEmpty() && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to add privilege " + role + " -> " + privilegeIds);
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
