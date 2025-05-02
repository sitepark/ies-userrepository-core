package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class RoleId implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String roleId;

  RoleId(@JsonProperty("roleId") String roleId) {
    Objects.requireNonNull(roleId, "roleId is null");
    this.roleId = roleId;
  }

  public String getRoleId() {
    return this.roleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.roleId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof RoleId that) && Objects.equals(this.roleId, that.roleId);
  }

  @Override
  public String toString() {
    return "RoleId{" + "roleId='" + roleId + '\'' + '}';
  }
}
