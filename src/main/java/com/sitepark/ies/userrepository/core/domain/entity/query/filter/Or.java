package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Objects;

public class Or implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<Filter> or;

  protected Or(@JsonProperty("or") Filter... or) {
    Objects.requireNonNull(or, "or is null");
    this.or = List.of(or);
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<Filter> getOr() {
    return this.or;
  }
}
