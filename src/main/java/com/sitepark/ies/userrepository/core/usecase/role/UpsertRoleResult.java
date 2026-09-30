package com.sitepark.ies.userrepository.core.usecase.role;

public sealed interface UpsertRoleResult {

  String roleId();

  record Created(String roleId, CreateRoleResult createRoleResult) implements UpsertRoleResult {}

  record Updated(String roleId, UpdateRoleResult updateRoleResult) implements UpsertRoleResult {}

  static Created created(String roleId, CreateRoleResult result) {
    return new Created(roleId, result);
  }

  static Updated updated(String roleId, UpdateRoleResult result) {
    return new Updated(roleId, result);
  }
}
