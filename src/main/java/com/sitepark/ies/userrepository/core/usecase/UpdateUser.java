package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class UpdateUser {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository repository;
  private final IdentifierResolver identifierResolver;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final ExtensionsNotifier extensionsNotifier;

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

  @SuppressWarnings("PMD.UseVarargs")
  public String updateUser(User user, @Nullable String[] roleIds) {

    User updateUser = this.buildUserWithId(user);
    if (updateUser.id() == null) {
      throw new IllegalArgumentException("The ID of the user must be set when updating.");
    }
    String id = updateUser.id();
    this.validateWritable();
    User storedUser = this.loadStoredUser(id);
    this.validateLogin(updateUser);

    User joinedUpdateUser = this.joinForUpdate(storedUser, updateUser);

    if (storedUser.equals(joinedUpdateUser)) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("update(unchanged): {}", joinedUpdateUser);
      }
    } else {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("update: {}", joinedUpdateUser);
      }
      this.repository.update(joinedUpdateUser);
      this.extensionsNotifier.notifyUpdated(joinedUpdateUser);
    }

    if (roleIds != null && roleIds.length > 0) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("reassign user to roles: {} -> {}", List.of(id), Arrays.asList(roleIds));
      }
      this.roleAssigner.reassignRoleToUsers(List.of(id), Arrays.asList(roleIds));
    }

    return id;
  }

  private User buildUserWithId(User user) {

    Identifier identifier = user.toIdentifier();
    if (identifier == null) {
      throw new IllegalArgumentException(
          "For users to be updated neither an id nor an anchor is set");
    }

    String id = this.identifierResolver.resolveIdentifier(identifier);

    return user.toBuilder().id(id).build();
  }

  private void validateWritable() {
    if (!this.accessControl.isUserWritable()) {
      throw new AccessDeniedException("Not allowed to update user");
    }
  }

  private User loadStoredUser(String id) {
    User storedUser = this.repository.get(id).orElseThrow(() -> new UserNotFoundException(id));

    assert storedUser.id() != null;
    List<String> roles = this.roleAssigner.getRolesAssignByUser(storedUser.id());
    return storedUser.toBuilder().roleIds(roles).build();
  }

  private void validateLogin(User user) {
    Optional<String> resolveLogin = this.repository.resolveLogin(user.login());
    if (resolveLogin.isPresent() && !resolveLogin.get().equals(user.id())) {
      throw new LoginAlreadyExistsException(user.login(), resolveLogin.get());
    }
  }

  private User joinForUpdate(User storedUser, User updateUser) {

    if (updateUser.anchor() == null && storedUser.anchor() != null) {
      return updateUser.toBuilder().anchor(storedUser.anchor()).build();
    }

    return updateUser;
  }
}
