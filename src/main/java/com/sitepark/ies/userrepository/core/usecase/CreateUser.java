package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.exception.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Password;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
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

public final class CreateUser {

  private final UserRepository repository;

  private final RoleAssigner roleAssigner;

  private final AccessControl accessControl;

  private final IdGenerator idGenerator;

  private final ExtensionsNotifier extensionsNotifier;

  private final PasswordHasher passwordHasher;

  private static final Logger LOGGER = LogManager.getLogger();

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

    if (newUser.getId().isPresent()) {
      throw new IllegalArgumentException("The ID of the user must not be set when creating.");
    }

    if (!this.accessControl.isUserCreatable()) {
      throw new AccessDeniedException("Not allowed to create user " + newUser);
    }

    this.validateAnchor(newUser);

    this.validateLogin(newUser);

    Password hashedPassword = null;
    if (newUser.getPassword().isPresent()) {
      hashedPassword = this.passwordHasher.hash(newUser.getPassword().get().getClearText());
    }

    String generatedId = this.idGenerator.generate();

    User userWithIdAndHashPassword =
        newUser.toBuilder().id(generatedId).password(hashedPassword).build();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create user: {}", userWithIdAndHashPassword);
    }

    this.repository.create(userWithIdAndHashPassword);

    this.roleAssigner.assignRoleToUser(
        userWithIdAndHashPassword.getRoleIds(), Collections.singletonList(generatedId));

    this.extensionsNotifier.notifyCreated(userWithIdAndHashPassword);

    return userWithIdAndHashPassword.getId().orElse("");
  }

  private void validateAnchor(User newUser) {
    if (newUser.getAnchor().isPresent()) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(newUser.getAnchor().get());
      if (anchorOwner.isPresent()) {
        throw new AnchorAlreadyExistsException(newUser.getAnchor().get(), anchorOwner.get());
      }
    }
  }

  private void validateLogin(User newUser) {
    Optional<String> resolveLogin = this.repository.resolveLogin(newUser.getLogin());
    if (resolveLogin.isPresent()) {
      throw new LoginAlreadyExistsException(newUser.getLogin(), resolveLogin.get());
    }
  }
}
