package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Objects;

public class PrivilegeIdList implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<String> privilegedList;

  protected PrivilegeIdList(@JsonProperty("privilegedList") String... privilegedList) {
    Objects.requireNonNull(privilegedList, "privilegedList is null");
    this.privilegedList = List.of(privilegedList);
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<String> getPrivilegedList() {
    return this.privilegedList;
  }
}
