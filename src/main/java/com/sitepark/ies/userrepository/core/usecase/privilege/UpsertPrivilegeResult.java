package com.sitepark.ies.userrepository.core.usecase.privilege;

import org.jetbrains.annotations.NotNull;

public sealed interface UpsertPrivilegeResult {

  public String privilegeId();

  record Created(@NotNull String privilegeId, @NotNull CreatePrivilegeResult createPrivilegeResult)
      implements UpsertPrivilegeResult {}

  record Updated(@NotNull String privilegeId, @NotNull UpdatePrivilegeResult updatePrivilegeResult)
      implements UpsertPrivilegeResult {}

  static Created created(@NotNull String privilegeId, @NotNull CreatePrivilegeResult result) {
    return new Created(privilegeId, result);
  }

  static Updated updated(@NotNull String privilegeId, @NotNull UpdatePrivilegeResult result) {
    return new Updated(privilegeId, result);
  }
}
