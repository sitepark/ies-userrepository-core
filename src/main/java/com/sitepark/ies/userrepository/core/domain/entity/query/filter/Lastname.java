package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Lastname implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String lastname;

  protected Lastname(@JsonProperty("lastname") String firstname) {
    Objects.requireNonNull(firstname, "lastname is null");
    this.lastname = firstname;
  }

  public String getLastname() {
    return this.lastname;
  }
}
