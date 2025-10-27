package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.ies.sharedkernel.json.UniquePropertyType;
import java.util.List;
import java.util.Objects;

@UniquePropertyType(uniqueProperty = "roleidlist")
public final class RoleIdList implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<String> roleIdList;

  RoleIdList(@JsonProperty("roleIdList") String... roleIdList) {
    Objects.requireNonNull(roleIdList, "roleIdList is null");
    this.roleIdList = List.of(roleIdList);
  }

  public List<String> getRoleIdList() {
    return List.copyOf(this.roleIdList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.roleIdList);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof RoleIdList that) && Objects.equals(this.roleIdList, that.roleIdList);
  }

  @Override
  public String toString() {
    return "RoleIdList{" + "roleIdList=" + roleIdList + '}';
  }
}
