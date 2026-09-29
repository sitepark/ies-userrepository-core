package com.sitepark.ies.userrepository.core.usecase.privilege;

public sealed interface UpsertPrivilegeResult {

  public String privilegeId();

  record Created(String privilegeId, CreatePrivilegeResult createPrivilegeResult)
      implements UpsertPrivilegeResult {}

  record Updated(String privilegeId, UpdatePrivilegeResult updatePrivilegeResult)
      implements UpsertPrivilegeResult {}

  static Created created(String privilegeId, CreatePrivilegeResult result) {
    return new Created(privilegeId, result);
  }

  static Updated updated(String privilegeId, UpdatePrivilegeResult result) {
    return new Updated(privilegeId, result);
  }
}
