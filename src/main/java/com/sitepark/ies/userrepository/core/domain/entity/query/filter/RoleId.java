package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class RoleId implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String roleId;

  protected RoleId(@JsonProperty("roleId") String roleId) {
    Objects.requireNonNull(roleId, "roleId is null");
    this.roleId = roleId;
  }

  public String getRoleId() {
    return this.roleId;
  }
}
