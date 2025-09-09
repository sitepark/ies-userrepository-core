package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogCommand;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RemoveRoles {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository repository;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  @Inject
  RemoveRoles(
      RoleRepository repository,
      AccessControl accessControl,
      AuditLogService auditLogService,
      Clock clock) {
    this.repository = repository;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public void removeRoles(@NotNull List<Identifier> identifiers) {

    if (identifiers.isEmpty()) {
      return;
    }

    if (!this.accessControl.isPrivilegeRemovable()) {
      throw new AccessDeniedException("Not allowed to remove role with identifiers " + identifiers);
    }

    List<Role> roles = identifiers.stream().map(this::loadRole).toList();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("remove roles: {}", identifiers);
    }

    this.repository.remove(roles.stream().map(Role::id).collect(Collectors.toList()));

    Instant now = Instant.now(this.clock);
    String batchId = this.auditLogService.generateAuditBatchId();
    roles.forEach(
        role ->
            this.auditLogService.createAuditLog(
                new CreateAuditLogCommand(
                    "Removed roles", "role", role.id(), "remove", "{}", "{}", now, batchId)));
  }

  private Role loadRole(@NotNull Identifier identifier) {
    final String id =
        identifier.resolveId(
            (anchor) ->
                this.repository
                    .resolveAnchor(identifier.getAnchor())
                    .orElseThrow(() -> new AnchorNotFoundException(identifier.getAnchor())));

    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("Privilege with id " + id + " not found."));
  }
}
