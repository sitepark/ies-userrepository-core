package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogCommand;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RemoveUsers {

  private final UserRepository repository;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  RemoveUsers(
      UserRepository repository,
      AccessControl accessControl,
      AuditLogService auditLogService,
      Clock clock) {

    this.repository = repository;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public void removeUsers(@NotNull List<Identifier> identifiers) {

    if (identifiers.isEmpty()) {
      return;
    }

    if (!this.accessControl.isUserRemovable()) {
      throw new AccessDeniedException("Not allowed to remove user with identifiers " + identifiers);
    }

    List<User> users = identifiers.stream().map(this::loadUser).toList();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("remove users: {}", identifiers);
    }

    this.repository.remove(users.stream().map(User::id).collect(Collectors.toList()));

    Instant now = Instant.now(this.clock);
    String batchId = this.auditLogService.generateAuditBatchId();
    users.forEach(
        user ->
            this.auditLogService.createAuditLog(
                new CreateAuditLogCommand(
                    "Removed user", "user", user.id(), "remove", "{}", "{}", now, batchId)));
  }

  private User loadUser(@NotNull Identifier identifier) {
    final String id =
        identifier.resolveId(
            (anchor) ->
                this.repository
                    .resolveAnchor(identifier.getAnchor())
                    .orElseThrow(() -> new AnchorNotFoundException(identifier.getAnchor())));

    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found."));
  }
}
