package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserSnapshot;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreateUserUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AssignRolesToUsersUseCase assignRolesToUsersUseCase;
  private final UserEntityAuthorizationService userEntityAuthorizationService;
  private final ExtensionsNotifier extensionsNotifier;
  private final Clock clock;

  @Inject
  CreateUserUseCase(
      UserRepository userRepository,
      RoleRepository roleRepository,
      AssignRolesToUsersUseCase assignRolesToUsersUseCase,
      UserEntityAuthorizationService userEntityAuthorizationService,
      ExtensionsNotifier extensionsNotifier,
      Clock clock) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.assignRolesToUsersUseCase = assignRolesToUsersUseCase;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
    this.extensionsNotifier = extensionsNotifier;
    this.clock = clock;
  }

  public CreateUserResult createUser(CreateUserRequest request) {

    this.validateUser(request.user());

    this.checkAccessControl(request.user());

    this.validateAnchor(request.user());

    this.validateLogin(request.user());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create user: {}", request.user());
    }

    Instant timestamp = Instant.now(this.clock);

    String id = this.userRepository.create(request.user());

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    User createdUser = request.user().toBuilder().id(id).build();
    UserSnapshot snapshot = new UserSnapshot(createdUser, roleIds);

    AssignRolesToUsersResult roleAssignmentResult = null;
    if (!roleIds.isEmpty()) {
      roleAssignmentResult =
          this.assignRolesToUsersUseCase.assignRolesToUsers(
              AssignRolesToUsersRequest.builder()
                  .userIdentifiers(b -> b.id(id))
                  .roleIdentifiers(b -> b.ids(roleIds))
                  .build());
    }

    this.extensionsNotifier.notifyCreated(createdUser);

    return new CreateUserResult(id, snapshot, roleAssignmentResult, timestamp);
  }

  private void validateUser(User user) {
    if (user.id() != null) {
      throw new IllegalArgumentException("The ID of the user must not be set when creating.");
    }
    if (user.lastName() == null || user.lastName().isBlank()) {
      throw new IllegalArgumentException("The last-name of the user must not be null or empty.");
    }
  }

  private void checkAccessControl(User user) {
    if (!this.userEntityAuthorizationService.isCreatable()) {
      throw new AccessDeniedException("Not allowed to create user " + user);
    }
  }

  private void validateAnchor(User user) {
    if (user.anchor() != null) {
      Optional<String> anchorOwner = this.userRepository.resolveAnchor(user.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(user.anchor(), owner);
          });
    }
  }

  private void validateLogin(User user) {
    Optional<String> resolveLogin = this.userRepository.resolveLogin(user.login());
    resolveLogin.ifPresent(
        owner -> {
          throw new LoginAlreadyExistsException(user.login(), owner);
        });
  }
}
