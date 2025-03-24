package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdateUser {

  private final UserRepository repository;

  private final IdentifierResolver identifierResolver;

  private final RoleAssigner roleAssigner;

  private final AccessControl accessControl;

  private final ExtensionsNotifier extensionsNotifier;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  UpdateUser(
      UserRepository repository,
      IdentifierResolver identifierResolver,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      ExtensionsNotifier extensionsNotifier) {
    this.repository = repository;
    this.identifierResolver = identifierResolver;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.extensionsNotifier = extensionsNotifier;
  }

  public String updateUser(User user) {

    User updateUser = this.buildUserWithId(user);
    assert updateUser.getId().isPresent();
    String id = updateUser.getId().get();
    this.validateWritable();
    User storedUser = this.loadStoredUser(id);
    this.validateLogin(updateUser);

    User joinedUpdateUser = this.joinForUpdate(storedUser, updateUser);

    if (storedUser.equals(joinedUpdateUser)) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("update(unchanged): {}", joinedUpdateUser);
      }
      return id;
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update: {}", joinedUpdateUser);
    }

    this.roleAssigner.reassignRoleToUser(joinedUpdateUser.getRoles(), List.of(id));

    this.repository.update(joinedUpdateUser);

    this.extensionsNotifier.notifyUpdated(joinedUpdateUser);

    return id;
  }

  private User buildUserWithId(User user) {

    if (user.getIdentifier().isEmpty()) {
      throw new IllegalArgumentException(
          "For users to be updated neither an id nor an anchor is set");
    }

    String id = this.identifierResolver.resolveIdentifier(user.getIdentifier().get());

    return user.toBuilder().id(id).build();
  }

  private void validateWritable() {
    if (!this.accessControl.isUserWritable()) {
      throw new AccessDeniedException("Not allowed to update user");
    }
  }

  private User loadStoredUser(String id) {
    User storedUser = this.repository.get(id).orElseThrow(() -> new UserNotFoundException(id));

    assert storedUser.getId().isPresent();
    List<Identifier> roles = this.roleAssigner.getRolesAssignByUser(storedUser.getId().get());
    return storedUser.toBuilder().roles(roles).build();
  }

  private void validateLogin(User user) {
    Optional<String> resolveLogin = this.repository.resolveLogin(user.getLogin());
    if (resolveLogin.isPresent() && !resolveLogin.equals(user.getId())) {
      throw new LoginAlreadyExistsException(user.getLogin(), resolveLogin.get());
    }
  }

  private User joinForUpdate(User storedUser, User updateUser) {

    if (updateUser.getAnchor().isEmpty() && storedUser.getAnchor().isPresent()) {
      return updateUser.toBuilder().anchor(storedUser.getAnchor().get()).build();
    }

    return updateUser;
  }
}
