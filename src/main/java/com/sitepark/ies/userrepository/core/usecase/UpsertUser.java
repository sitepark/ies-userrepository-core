package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UpsertUser {

  private final AccessControl accessControl;
  private final UserRepository repository;
  private final CreateUser createUserUseCase;
  private final UpdateUser updateUserUseCase;

  @Inject
  UpsertUser(
      AccessControl accessControl,
      UserRepository repository,
      CreateUser createUserUseCase,
      UpdateUser updateUserUseCase) {
    this.accessControl = accessControl;
    this.repository = repository;
    this.createUserUseCase = createUserUseCase;
    this.updateUserUseCase = updateUserUseCase;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String upsertUser(@NotNull User user, @Nullable String[] roleIds) {

    if (!this.accessControl.isUserCreatable() || !this.accessControl.isUserWritable()) {
      throw new AccessDeniedException("Not allowed to upsert user " + user);
    }

    User userResolved = this.toUserWithId(user);
    if (userResolved.id() == null) {
      return this.createUserUseCase.createUser(userResolved, roleIds);
    } else {
      return this.updateUserUseCase.updateUser(userResolved, roleIds);
    }
  }

  private User toUserWithId(User user) {
    if (user.id() == null && user.anchor() != null) {
      return this.repository
          .resolveAnchor(user.anchor())
          .map(s -> user.toBuilder().id(s).build())
          .orElse(user);
    } else if (user.id() != null && user.anchor() != null) {
      this.repository
          .resolveAnchor(user.anchor())
          .ifPresent(
              owner -> {
                if (!owner.equals(user.id())) {
                  throw new AnchorAlreadyExistsException(user.anchor(), owner);
                }
              });
    }
    return user;
  }
}
