package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class PrivilegeId implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String privilegeId;

  protected PrivilegeId(@JsonProperty("privilegeId") String privilegeId) {
    Objects.requireNonNull(privilegeId, "privilegeId is null");
    this.privilegeId = privilegeId;
  }

  public String getPrivilegeId() {
    return this.privilegeId;
  }
}
