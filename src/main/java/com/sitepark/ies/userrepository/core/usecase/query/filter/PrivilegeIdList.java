package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.ies.sharedkernel.json.UniquePropertyType;
import java.util.List;
import java.util.Objects;

@UniquePropertyType(uniqueProperty = "privilegeidlist")
public final class PrivilegeIdList implements Filter {

  private final List<String> privilegedList;

  PrivilegeIdList(@JsonProperty("privilegedList") String... privilegedList) {
    Objects.requireNonNull(privilegedList, "privilegedList is null");
    this.privilegedList = List.of(privilegedList);
  }

  public List<String> getPrivilegedList() {
    return List.copyOf(this.privilegedList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.privilegedList);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof PrivilegeIdList that)
        && Objects.equals(this.privilegedList, that.privilegedList);
  }

  @Override
  public String toString() {
    return "PrivilegeIdList{" + "privilegedList=" + privilegedList + '}';
  }
}
