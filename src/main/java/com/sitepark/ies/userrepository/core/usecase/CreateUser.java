package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.IdGenerator;
import com.sitepark.ies.userrepository.core.port.PasswordHasher;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class CreateUser {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final IdGenerator idGenerator;
  private final ExtensionsNotifier extensionsNotifier;
  private final PasswordHasher passwordHasher;

  @Inject
  CreateUser(
      UserRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      IdGenerator idGenerator,
      ExtensionsNotifier extensionsNotifier,
      PasswordHasher passwordHasher) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.idGenerator = idGenerator;
    this.extensionsNotifier = extensionsNotifier;
    this.passwordHasher = passwordHasher;
  }

  public String createUser(User newUser) {

    if (newUser.getId() != null) {
      throw new IllegalArgumentException("The ID of the user must not be set when creating.");
    }

    if (!this.accessControl.isUserCreatable()) {
      throw new AccessDeniedException("Not allowed to create user " + newUser);
    }

    this.validateAnchor(newUser);

    this.validateLogin(newUser);

    Password hashedPassword = this.hashPassword(newUser.getPassword());

    String generatedId = this.idGenerator.generate();

    User userWithIdAndHashPassword =
        newUser.toBuilder().id(generatedId).password(hashedPassword).build();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create user: {}", userWithIdAndHashPassword);
    }

    this.repository.create(userWithIdAndHashPassword);

    this.roleAssigner.assignUsersToRoles(
        userWithIdAndHashPassword.getRoleIds(), Collections.singletonList(generatedId));

    this.extensionsNotifier.notifyCreated(userWithIdAndHashPassword);

    return userWithIdAndHashPassword.getId();
  }

  private void validateAnchor(User newUser) {
    if (newUser.getAnchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(newUser.getAnchor());
      if (anchorOwner.isPresent()) {
        throw new AnchorAlreadyExistsException(newUser.getAnchor(), anchorOwner.get());
      }
    }
  }

  private void validateLogin(User newUser) {
    Optional<String> resolveLogin = this.repository.resolveLogin(newUser.getLogin());
    if (resolveLogin.isPresent()) {
      throw new LoginAlreadyExistsException(newUser.getLogin(), resolveLogin.get());
    }
  }

  @Nullable
  private Password hashPassword(Password password) {
    if (password == null) {
      return null;
    }
    return this.passwordHasher.hash(password.getClearText());
  }
}
