package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.base.Identifier;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RemovePrivileges {

  private static final Logger LOGGER = LogManager.getLogger();

  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  private static final String BUILT_IN_PRIVILEGE_ID_FULL_ACCESS = "1";

  @Inject
  RemovePrivileges(
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

  public void removePrivileges(@NotNull RemovePrivilegesRequest request) {

    if (request.identifiers().isEmpty()) {
      return;
    }

    if (!this.accessControl.isPrivilegeRemovable()) {
      throw new AccessDeniedException(
          "Not allowed to remove privilege with identifiers " + request.identifiers());
    }

    Instant now = Instant.now(this.clock);
    String parentId =
        request.identifiers().size() > 1
            ? createBatchRemoveLog(now, request.auditParentId())
            : request.auditParentId();

    for (Identifier identifier : request.identifiers()) {
      String id = this.toId(identifier);
      if (BUILT_IN_PRIVILEGE_ID_FULL_ACCESS.equals(id)) {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("Skipping removal of built-in privilege with id 1 (FULL_ACCESS).");
        }
        continue;
      }
      this.removePrivilege(id, now, parentId);
    }
  }

  private String toId(Identifier identifier) {
    return identifier.resolveId(
        anchor ->
            this.repository
                .resolveAnchor(anchor)
                .orElseThrow(() -> new AnchorNotFoundException(anchor)));
  }

  private void removePrivilege(String id, Instant now, String parentId) {

    PrivilegeSnapshot snapshot = this.createSnapshot(id);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("remove privilege: {}", id);
    }

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(snapshot, now, parentId);
    this.repository.remove(id);
    this.auditLogService.createAuditLog(createAuditLogRequest);
  }

  private String createBatchRemoveLog(Instant now, String parentId) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.PRIVILEGE.name(),
            null,
            null,
            AuditLogAction.BATCH_REMOVE.name(),
            null,
            null,
            now,
            parentId));
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      PrivilegeSnapshot snapshot, Instant now, String parentId) {

    String backwardData;
    try {
      backwardData = this.auditLogService.serialize(snapshot);
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
        AuditLogAction.REMOVE.name(),
        backwardData,
        null,
        now,
        parentId);
  }

  private PrivilegeSnapshot createSnapshot(String id) {
    Privilege privilege = this.loadPrivilege(id);
    List<String> roleIds = this.roleAssigner.getRolesAssignByPrivilege(id);
    return new PrivilegeSnapshot(privilege, roleIds);
  }

  private Privilege loadPrivilege(String id) {
    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("Privilege with id " + id + " not found."));
  }
}
