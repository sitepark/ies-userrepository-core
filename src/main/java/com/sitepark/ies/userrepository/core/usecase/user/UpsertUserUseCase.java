package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;

public final class UpsertUserUseCase {

  private final AccessControl accessControl;
  private final UserRepository repository;
  private final CreateUserUseCase createUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;

  @Inject
  UpsertUserUseCase(
      AccessControl accessControl,
      UserRepository repository,
      CreateUserUseCase createUserUseCase,
      UpdateUserUseCase updateUserUseCase) {
    this.accessControl = accessControl;
    this.repository = repository;
    this.createUserUseCase = createUserUseCase;
    this.updateUserUseCase = updateUserUseCase;
  }

  public String upsertUser(UpsertUserRequest request) {

    if (!this.accessControl.isUserCreatable() || !this.accessControl.isUserWritable()) {
      throw new AccessDeniedException("Not allowed to upsert user " + request.user());
    }

    User userResolved = this.toUserWithId(request.user());
    if (userResolved.id() == null) {
      return this.createUserUseCase
          .createUser(
              CreateUserRequest.builder()
                  .user(userResolved)
                  .roleIdentifiers(r -> r.identifiers(request.roleIdentifiers()))
                  .auditParentId(request.auditParentId())
                  .build())
          .userId();
    } else {
      return this.updateUserUseCase
          .updateUser(
              UpdateUserRequest.builder()
                  .user(userResolved)
                  .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers()))
                  .auditParentId(request.auditParentId())
                  .build())
          .userId();
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
