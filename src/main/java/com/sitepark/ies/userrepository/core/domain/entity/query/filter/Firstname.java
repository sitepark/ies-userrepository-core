package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Firstname implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String firstname;

  protected Firstname(@JsonProperty("firstname") String firstname) {
    Objects.requireNonNull(firstname, "firstname is null");
    this.firstname = firstname;
  }

  public String getFirstname() {
    return this.firstname;
  }
}
