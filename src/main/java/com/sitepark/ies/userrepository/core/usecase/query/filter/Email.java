package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class Email implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String email;

  Email(@JsonProperty("email") String email) {
    Objects.requireNonNull(email, "email is null");
    this.email = email;
  }

  public String getEmail() {
    return this.email;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.email);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Email that) && Objects.equals(this.email, that.email);
  }

  @Override
  public String toString() {
    return "Email{" + "email='" + email + '\'' + '}';
  }
}
