package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class FirstName implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String firstName;

  FirstName(@JsonProperty("firstName") String firstName) {
    Objects.requireNonNull(firstName, "firstName is null");
    this.firstName = firstName;
  }

  public String getFirstName() {
    return this.firstName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.firstName);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof FirstName that) && Objects.equals(this.firstName, that.firstName);
  }

  @Override
  public String toString() {
    return "FirstName{" + "firstName='" + firstName + '\'' + '}';
  }
}
