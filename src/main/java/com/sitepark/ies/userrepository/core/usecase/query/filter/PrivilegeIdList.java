package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.ies.sharedkernel.json.UniquePropertyType;
import java.util.List;
import java.util.Objects;

@UniquePropertyType(uniqueProperty = "privilegeidlist")
public final class PrivilegeIdList implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<String> privilegeIdList;

  PrivilegeIdList(@JsonProperty("privilegeIdList") String... privilegeIdList) {
    Objects.requireNonNull(privilegeIdList, "privilegeIdList is null");
    this.privilegeIdList = List.of(privilegeIdList);
  }

  public List<String> getPrivilegeIdList() {
    return List.copyOf(this.privilegeIdList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.privilegeIdList);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof PrivilegeIdList that)
        && Objects.equals(this.privilegeIdList, that.privilegeIdList);
  }

  @Override
  public String toString() {
    return "PrivilegeIdList{" + "privilegeIdList=" + privilegeIdList + '}';
  }
}
