package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class And implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<Filter> and;

  And(@JsonProperty("and") Filter... and) {
    Objects.requireNonNull(and, "and is null");
    if (and.length == 0) {
      throw new IllegalArgumentException("and is empty");
    }
    this.and = List.of(and);
  }

  public List<Filter> getAnd() {
    return List.copyOf(this.and);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.and);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof And that) && Objects.equals(this.and, that.and);
  }

  @Override
  public String toString() {
    return "And{" + "and=" + and + '}';
  }
}
