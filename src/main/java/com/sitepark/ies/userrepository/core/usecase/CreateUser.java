package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.exception.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.IdGenerator;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreateUser {

  private final UserRepository repository;

  private final RoleAssigner roleAssigner;

  private final AccessControl accessControl;

  private final IdGenerator idGenerator;

  private final ExtensionsNotifier extensionsNotifier;

  private static Logger LOGGER = LogManager.getLogger();

  @Inject
  protected CreateUser(
      UserRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      IdGenerator idGenerator,
      ExtensionsNotifier extensionsNotifier) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.idGenerator = idGenerator;
    this.extensionsNotifier = extensionsNotifier;
  }

  public String createUser(User newUser) {

    if (newUser.getId().isPresent()) {
      throw new IllegalArgumentException("The ID of the user must not be set when creating.");
    }

    this.validateAnchor(newUser);

    this.validateLogin(newUser);

    if (!this.accessControl.isUserCreateable()) {
      throw new AccessDeniedException("Not allowed to create user " + newUser);
    }

    String generatedId = this.idGenerator.generate();

    User userWithId = newUser.toBuilder().id(generatedId).build();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create user: {}", userWithId);
    }

    this.repository.create(userWithId);

    this.roleAssigner.assignRoleToUser(userWithId.getRoleList(), Arrays.asList(generatedId));

    this.extensionsNotifier.notifyCreated(userWithId);

    return userWithId.getId().get();
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
