package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogCommand;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RemovePrivileges {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;

  @Inject
  RemovePrivileges(
      PrivilegeRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      AuditLogService auditLogService) {
    this.repository = repository;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
  }

  public void removePrivileges(@NotNull List<Identifier> identifier) {

    if (identifier.isEmpty()) {
      return;
    }

    if (!this.accessControl.isPrivilegeRemovable()) {
      throw new AccessDeniedException(
          "Not allowed to remove privilege with identifier " + identifier);
    }

    List<Privilege> privileges = identifier.stream().map(this::loadPrivilege).toList();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("remove privileges: {}", identifier);
    }

    this.repository.remove(privileges.stream().map(Privilege::getId).collect(Collectors.toList()));

    OffsetDateTime now = OffsetDateTime.now();
    privileges.forEach(
        privilege ->
            this.auditLogService.createAuditLog(
                new CreateAuditLogCommand(
                    "Removed privileges",
                    "privilege",
                    privilege.getId(),
                    "remove",
                    "{}",
                    "{}",
                    now,
                    null)));
  }

  private Privilege loadPrivilege(@NotNull Identifier identifier) {
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
