package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class PrivilegeId implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String privilegeId;

  PrivilegeId(@JsonProperty("privilegeId") String privilegeId) {
    Objects.requireNonNull(privilegeId, "privilegeId is null");
    this.privilegeId = privilegeId;
  }

  public String getPrivilegeId() {
    return this.privilegeId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.privilegeId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof PrivilegeId that) && Objects.equals(this.privilegeId, that.privilegeId);
  }

  @Override
  public String toString() {
    return "PrivilegeId{" + "privilegeId='" + privilegeId + '\'' + '}';
  }
}
