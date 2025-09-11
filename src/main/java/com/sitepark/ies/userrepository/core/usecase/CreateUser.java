package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.PasswordHasher;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class CreateUser {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final ExtensionsNotifier extensionsNotifier;
  private final PasswordHasher passwordHasher;

  @Inject
  CreateUser(
      UserRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      ExtensionsNotifier extensionsNotifier,
      PasswordHasher passwordHasher) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.extensionsNotifier = extensionsNotifier;
    this.passwordHasher = passwordHasher;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String createUser(User newUser, @Nullable String[] roleIds) {

    this.validateUser(newUser);

    this.checkAccessControl(newUser);

    this.validateAnchor(newUser);

    this.validateLogin(newUser);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create user: {}", newUser);
    }

    Password hashedPassword = this.hashPassword(newUser.password());

    User userWithIdAndHashPassword = newUser.toBuilder().password(hashedPassword).build();

    String id = this.repository.create(userWithIdAndHashPassword);

    if (roleIds != null && roleIds.length > 0) {
      this.roleAssigner.assignRolesToUsers(List.of(id), Arrays.asList(roleIds));
    }

    this.extensionsNotifier.notifyCreated(userWithIdAndHashPassword.toBuilder().id(id).build());

    return id;
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

  private void validateLogin(User user) {
    Optional<String> resolveLogin = this.repository.resolveLogin(user.login());
    resolveLogin.ifPresent(
        owner -> {
          throw new LoginAlreadyExistsException(user.login(), owner);
        });
  }

  @Nullable
  private Password hashPassword(Password password) {
    if (password == null) {
      return null;
    }
    return this.passwordHasher.hash(password.getClearText());
  }
}
