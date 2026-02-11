package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserSnapshot;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RestoreUserUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository repository;
  private final RoleAssigner roleAssigner;
  private final UserEntityAuthorizationService userEntityAuthorizationService;
  private final Clock clock;

  @Inject
  RestoreUserUseCase(
      UserRepository repository,
      RoleAssigner roleAssigner,
      UserEntityAuthorizationService userEntityAuthorizationService,
      Clock clock) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
    this.clock = clock;
  }

  public RestoreUserResult restoreUser(RestoreUserRequest request) {

    User user = request.data().user();
    List<String> roleIds = request.data().roleIds();

    this.validateUser(user);

    this.checkAccessControl(user);

    if (this.repository.get(user.id()).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, user with ID {} already exists.", user.id());
      }
      return RestoreUserResult.skipped(user.id(), "User with ID " + user.id() + " already exists");
    }

    this.validateAnchor(user);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("user role: {}", user);
    }

    Instant timestamp = Instant.now(this.clock);

    UserSnapshot snapshot = new UserSnapshot(user, roleIds);

    this.repository.restore(user);
    if (!roleIds.isEmpty()) {
      String userId = user.id();
      assert userId != null : "user.id() was validated in validateUser()";
      this.roleAssigner.assignRolesToUsers(List.of(userId), roleIds);
    }

    return RestoreUserResult.restored(user.id(), snapshot, timestamp);
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
    if (!this.userEntityAuthorizationService.isCreatable()) {
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
}
