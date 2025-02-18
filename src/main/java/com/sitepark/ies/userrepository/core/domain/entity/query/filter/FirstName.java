package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class FirstName implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String firstName;

  protected FirstName(@JsonProperty("firstName") String firstName) {
    Objects.requireNonNull(firstName, "firstName is null");
    this.firstName = firstName;
  }

  public String getFirstName() {
    return this.firstName;
  }
}
