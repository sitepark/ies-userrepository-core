package com.sitepark.ies.userrepository.core.usecase.role;

import org.jetbrains.annotations.NotNull;

public sealed interface UpsertRoleResult {

  String roleId();

  record Created(@NotNull String roleId, @NotNull CreateRoleResult createRoleResult)
      implements UpsertRoleResult {}

  record Updated(@NotNull String roleId, @NotNull UpdateRoleResult updateRoleResult)
      implements UpsertRoleResult {}

  static Created created(@NotNull String roleId, @NotNull CreateRoleResult result) {
    return new Created(roleId, result);
  }

  static Updated updated(@NotNull String roleId, @NotNull UpdateRoleResult result) {
    return new Updated(roleId, result);
  }
}
