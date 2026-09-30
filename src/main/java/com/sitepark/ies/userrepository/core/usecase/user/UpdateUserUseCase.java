package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdateUserUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final ReassignRolesToUsersUseCase reassignRolesToUsersUseCase;
  private final UserRepository userRepository;
  private final UserEntityAuthorizationService userEntityAuthorizationService;
  private final ExtensionsNotifier extensionsNotifier;
  private final PatchService<User> patchService;
  private final Clock clock;

  @Inject
  UpdateUserUseCase(
      ReassignRolesToUsersUseCase reassignRolesToUsersUseCase,
      UserRepository userRepository,
      UserEntityAuthorizationService userEntityAuthorizationService,
      ExtensionsNotifier extensionsNotifier,
      PatchServiceFactory patchServiceFactory,
      Clock clock) {
    this.reassignRolesToUsersUseCase = reassignRolesToUsersUseCase;
    this.userRepository = userRepository;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
    this.extensionsNotifier = extensionsNotifier;
    this.patchService = patchServiceFactory.createPatchService(User.class);
    this.clock = clock;
  }

  public UpdateUserResult updateUser(UpdateUserRequest request) {

    User newUser;
    if (request.user().id() == null) {
      newUser = this.toUserWithId(request.user());
    } else {
      this.validateAnchor(request.user());
      newUser = request.user();
    }

    String userId = Objects.requireNonNull(newUser.id(), "id is null");
    this.checkAuthorization(newUser, userId);
    this.validateLogin(newUser);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update user: {}", newUser);
    }

    User oldUser =
        this.userRepository
            .get(userId)
            .orElseThrow(() -> new UserNotFoundException("No user with ID " + userId + " found."))
            .toBuilder()
            .build();

    Instant timestamp = Instant.now(this.clock);

    User joinedUser = this.joinForUpdate(oldUser, newUser);

    PatchDocument patch = this.patchService.createPatch(oldUser, joinedUser);

    // Determine user update result
    UserUpdateResult userUpdateResult;
    if (patch.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip user update, user with ID {} is unchanged.", joinedUser.id());
      }
      userUpdateResult = UserUpdateResult.unchanged();
    } else {
      User userForUpdate = joinedUser.toBuilder().changedAt(timestamp).build();
      this.userRepository.update(userForUpdate);
      this.extensionsNotifier.notifyUpdated(userForUpdate);

      PatchDocument revertPatch = this.patchService.createPatch(joinedUser, oldUser);
      userUpdateResult =
          UserUpdateResult.updated(userForUpdate.toDisplayName(), patch, revertPatch);
    }

    // Handle role assignments independently
    ReassignRolesToUsersResult roleReassignmentResult;
    if (request.roleIdentifiers().shouldUpdate()) {
      roleReassignmentResult =
          this.reassignRolesToUsersUseCase.reassignRolesToUsers(
              ReassignRolesToUsersRequest.builder()
                  .userIdentifiers(b -> b.id(userId))
                  .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers().getValue()))
                  .build());
    } else {
      roleReassignmentResult = ReassignRolesToUsersResult.skipped();
    }

    return new UpdateUserResult(userId, timestamp, userUpdateResult, roleReassignmentResult);
  }

  private User toUserWithId(User user) {
    if (user.id() == null) {
      Anchor anchor = user.anchor();
      if (anchor != null) {
        String id =
            this.userRepository
                .resolveAnchor(anchor)
                .orElseThrow(() -> new AnchorNotFoundException(anchor));
        return user.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException("Neither id nor anchor is specified to update the user.");
    }
    return user;
  }

  private void validateAnchor(User user) {
    Anchor anchor = user.anchor();
    if (anchor != null) {
      Optional<String> anchorOwner = this.userRepository.resolveAnchor(anchor);
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(user.id())) {
              throw new AnchorAlreadyExistsException(anchor, owner);
            }
          });
    }
  }

  private void checkAuthorization(User user, String id) {
    if (!this.userEntityAuthorizationService.isWritable(id)) {
      throw new AccessDeniedException("Not allowed to update user " + user);
    }
  }

  private void validateLogin(User user) {
    Optional<String> resolveLogin = this.userRepository.resolveLogin(user.login());
    if (resolveLogin.isPresent() && !resolveLogin.get().equals(user.id())) {
      throw new LoginAlreadyExistsException(user.login(), resolveLogin.get());
    }
  }

  private User joinForUpdate(User storedUser, User updateUser) {
    User.Builder builder = updateUser.toBuilder();
    if (updateUser.anchor() == null && storedUser.anchor() != null) {
      builder.anchor(storedUser.anchor()).build();
    }
    builder.createdAt(Objects.requireNonNull(storedUser.createdAt(), "createdAt is null"));
    builder.changedAt(Objects.requireNonNull(storedUser.changedAt(), "changedAt is null"));

    return builder.build();
  }
}
