package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class LastName implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String lastName;

  LastName(@JsonProperty("lastName") String lastName) {
    Objects.requireNonNull(lastName, "last name is null");
    this.lastName = lastName;
  }

  public String getLastName() {
    return this.lastName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.lastName);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof LastName that) && Objects.equals(this.lastName, that.lastName);
  }

  @Override
  public String toString() {
    return "LastName{" + "lastName='" + lastName + '\'' + '}';
  }
}
