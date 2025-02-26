package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Objects;

public class RoleIdList implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<String> roleIdList;

  protected RoleIdList(@JsonProperty("roleIdList") String... roleIdList) {
    Objects.requireNonNull(roleIdList, "roleIdList is null");
    this.roleIdList = List.of(roleIdList);
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<String> getRoleIdList() {
    return this.roleIdList;
  }
}
