package com.sitepark.ies.userrepository.core.usecase.user;

import org.jetbrains.annotations.NotNull;

public sealed interface UpsertUserResult {

  record Created(@NotNull String userId, @NotNull CreateUserResult createUserResult)
      implements UpsertUserResult {}

  record Updated(@NotNull String userId, @NotNull UpdateUserResult updateUserResult)
      implements UpsertUserResult {}

  static Created created(@NotNull String userId, @NotNull CreateUserResult result) {
    return new Created(userId, result);
  }

  static Updated updated(@NotNull String userId, @NotNull UpdateUserResult result) {
    return new Updated(userId, result);
  }
}
