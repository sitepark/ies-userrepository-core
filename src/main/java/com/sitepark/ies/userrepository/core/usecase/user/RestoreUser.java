package com.sitepark.ies.userrepository.core.usecase.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RestoreUser {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  @Inject
  RestoreUser(
      UserRepository repository,
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

  public void restoreUser(RestoreUserRequest request) {

    User user = request.data().user();
    List<String> roleIds = request.data().roleIds();
    String auditBatchId = request.auditParentId();

    this.validateUser(user);

    this.checkAccessControl(user);

    if (this.repository.get(user.id()).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, user with ID {} already exists.", user.id());
      }
      return;
    }

    this.validateAnchor(user);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("user role: {}", user);
    }

    Instant now = Instant.now(this.clock);

    UserSnapshot restoreData = new UserSnapshot(user, roleIds);

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(restoreData, now, auditBatchId);

    this.repository.restore(user);
    if (!roleIds.isEmpty()) {
      String userId = user.id();
      assert userId != null : "user.id() was validated in validateUser()";
      this.roleAssigner.assignRolesToUsers(List.of(userId), roleIds);
    }

    this.auditLogService.createAuditLog(createAuditLogRequest);
  }

  private void validateUser(User user) {
    if (user.id() == null || user.id().isBlank()) {
      throw new IllegalArgumentException("The id of the user must not be null or empty.");
    }
    if (user.login() == null || user.login().isBlank()) {
      throw new IllegalArgumentException("The login of the user must not be null or empty.");
    }
  }

  private void checkAccessControl(User user) {
    if (!this.accessControl.isUserCreatable()) {
      throw new AccessDeniedException("Not allowed to create user " + user);
    }
  }

  private void validateAnchor(User user) {
    if (user.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(user.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(user.anchor(), owner);
          });
    }
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      UserSnapshot data, Instant now, String auditLogParentId) {
    String json = serializeUser(data);
    return new CreateAuditLogRequest(
        AuditLogEntityType.USER.name(),
        data.user().id(),
        data.user().toDisplayName(),
        AuditLogAction.RESTORE.name(),
        null,
        json,
        now,
        auditLogParentId);
  }

  private String serializeUser(UserSnapshot data) {
    try {
      return this.objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.USER.name(), data.user().id(), data.user().toDisplayName(), e);
    }
  }
}
