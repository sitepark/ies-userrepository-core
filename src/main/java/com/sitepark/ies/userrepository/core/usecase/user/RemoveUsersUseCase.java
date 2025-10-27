package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RemoveUsersUseCase {

  private final UserRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  private static final String BUILT_IN_USER_ID_ADMINISTRATOR = "1";

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  RemoveUsersUseCase(
      UserRepository repository,
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

  public void removeUsers(RemoveUsersRequest request) {

    if (request.identifiers().isEmpty()) {
      return;
    }

    if (!this.accessControl.isUserRemovable()) {
      throw new AccessDeniedException(
          "Not allowed to remove user with identifiers " + request.identifiers());
    }

    Instant now = Instant.now(this.clock);
    String parentId =
        request.identifiers().size() > 1
            ? createBatchRemoveLog(now, request.auditParentId())
            : request.auditParentId();

    IdentifierResolver identifierResolver = IdentifierResolver.create(this.repository);
    for (Identifier identifier : request.identifiers()) {
      String id = identifierResolver.resolve(identifier);
      if (BUILT_IN_USER_ID_ADMINISTRATOR.equals(id)) {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("Skipping removal of built-in role with id 1 (Administrator).");
        }
        continue;
      }
      this.removeUser(id, now, parentId);
    }
  }

  private void removeUser(String id, Instant now, String parentId) {

    UserSnapshot snapshot = this.createSnapshot(id);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("remove user: {}", id);
    }

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(snapshot, now, parentId);
    this.repository.remove(id);
    this.auditLogService.createAuditLog(createAuditLogRequest);
  }

  private String createBatchRemoveLog(Instant now, String parentId) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            null,
            null,
            AuditLogAction.BATCH_REMOVE.name(),
            null,
            null,
            now,
            parentId));
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      UserSnapshot snapshot, Instant now, String parentId) {

    String backwardData;
    try {
      backwardData = this.auditLogService.serialize(snapshot);
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.USER.name(), snapshot.user().id(), snapshot.user().toDisplayName(), e);
    }

    return new CreateAuditLogRequest(
        AuditLogEntityType.USER.name(),
        snapshot.user().id(),
        snapshot.user().toDisplayName(),
        AuditLogAction.REMOVE.name(),
        backwardData,
        null,
        now,
        parentId);
  }

  private UserSnapshot createSnapshot(String id) {
    User user = this.loadUser(id);
    List<String> roleIds = this.roleAssigner.getRolesAssignByUser(id);
    return new UserSnapshot(user, roleIds);
  }

  private User loadUser(String id) {
    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found."));
  }
}
