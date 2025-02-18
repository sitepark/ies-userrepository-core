package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class LastName implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String lastName;

  protected LastName(@JsonProperty("lastName") String lastName) {
    Objects.requireNonNull(lastName, "last name is null");
    this.lastName = lastName;
  }

  public String getLastName() {
    return this.lastName;
  }
}
