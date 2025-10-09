package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.ies.sharedkernel.json.UniquePropertyType;
import java.util.Objects;

@UniquePropertyType(uniqueProperty = "firstname")
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
