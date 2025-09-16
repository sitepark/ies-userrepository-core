package com.sitepark.ies.userrepository.core.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RemovePrivileges {

  private static final Logger LOGGER = LogManager.getLogger();

  private final PrivilegeRepository repository;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  @Inject
  RemovePrivileges(
      PrivilegeRepository repository,
      AccessControl accessControl,
      AuditLogService auditLogService,
      ObjectMapper objectMapper,
      Clock clock) {
    this.repository = repository;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  public void removePrivileges(@NotNull List<Identifier> identifiers) {

    if (identifiers.isEmpty()) {
      return;
    }

    if (!this.accessControl.isPrivilegeRemovable()) {
      throw new AccessDeniedException(
          "Not allowed to remove privilege with identifiers " + identifiers);
    }

    Instant now = Instant.now(this.clock);
    String batchId = identifiers.size() > 1 ? createAuditBatch(now) : null;

    for (Identifier identifier : identifiers) {
      String id = this.toId(identifier);
      this.removePrivilege(id, now, batchId);
    }
  }

  private String toId(Identifier identifier) {
    return identifier.resolveId(
        anchor ->
            this.repository
                .resolveAnchor(anchor)
                .orElseThrow(() -> new AnchorNotFoundException(anchor)));
  }

  private void removePrivilege(String id, Instant now, String batchId) {

    Privilege privilege = this.loadPrivilege(id);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("remove privilege: {}", privilege);
    }

    CreateAuditLogRequest createAuditLogCommand =
        this.buildCreateAuditLogRequest(privilege, now, batchId);
    this.repository.remove(privilege.id());
    this.auditLogService.createAuditLog(createAuditLogCommand);
  }

  private Privilege loadPrivilege(String id) {
    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("Privilege with id " + id + " not found."));
  }

  private String createAuditBatch(Instant now) {
    return this.auditLogService.createAuditBatch(now);
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      Privilege privilege, Instant now, String batchId) {
    String json = serializePrivilege(privilege);
    return new CreateAuditLogRequest(
        AuditLogEntityType.PRIVILEGE.name(),
        privilege.id(),
        AuditLogAction.REMOVE.name(),
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
