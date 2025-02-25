package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Id implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String id;

  protected Id(@JsonProperty("id") String id) {
    Objects.requireNonNull(id, "id is null");
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
