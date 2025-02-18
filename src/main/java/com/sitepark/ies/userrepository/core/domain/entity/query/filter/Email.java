package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Email implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String email;

  protected Email(@JsonProperty("email") String email) {
    Objects.requireNonNull(email, "email is null");
    this.email = email;
  }

  public String getEmail() {
    return this.email;
  }
}
