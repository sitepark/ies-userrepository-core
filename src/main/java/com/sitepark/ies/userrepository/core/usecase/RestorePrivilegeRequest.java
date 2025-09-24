package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RestorePrivilegeRequest(
    Privilege privilege, String[] roleIds, @Nullable String auditParentId) {

  public RestorePrivilegeRequest {
    roleIds = roleIds != null ? roleIds.clone() : new String[0];
  }

  @NotNull
  public String[] roleIds() {
    return roleIds.clone();
  }
}
