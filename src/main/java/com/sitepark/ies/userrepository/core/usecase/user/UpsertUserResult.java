package com.sitepark.ies.userrepository.core.usecase.user;

public sealed interface UpsertUserResult {

  public String userId();

  record Created(String userId, CreateUserResult createUserResult) implements UpsertUserResult {}

  record Updated(String userId, UpdateUserResult updateUserResult) implements UpsertUserResult {}

  static Created created(String userId, CreateUserResult result) {
    return new Created(userId, result);
  }

  static Updated updated(String userId, UpdateUserResult result) {
    return new Updated(userId, result);
  }
}
