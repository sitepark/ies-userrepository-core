package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.user.UpsertUserResult.Created;
import com.sitepark.ies.userrepository.core.usecase.user.UpsertUserResult.Updated;
import jakarta.inject.Inject;

public final class UpsertUserUseCase {

  private final UserRepository repository;
  private final CreateUserUseCase createUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;

  @Inject
  UpsertUserUseCase(
      UserRepository repository,
      CreateUserUseCase createUserUseCase,
      UpdateUserUseCase updateUserUseCase) {
    this.repository = repository;
    this.createUserUseCase = createUserUseCase;
    this.updateUserUseCase = updateUserUseCase;
  }

  public UpsertUserResult upsertUser(UpsertUserRequest request) {

    User userResolved = this.toUserWithId(request.user());
    if (userResolved.id() == null) {
      CreateUserResult result =
          this.createUserUseCase.createUser(
              CreateUserRequest.builder()
                  .user(userResolved)
                  .roleIdentifiers(r -> r.identifiers(request.roleIdentifiers().getValue()))
                  .build());
      return new Created(result.userId(), result);
    } else {
      UpdateUserResult result =
          this.updateUserUseCase.updateUser(
              UpdateUserRequest.builder()
                  .user(userResolved)
                  .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers().getValue()))
                  .build());
      return new Updated(userResolved.id(), result);
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
