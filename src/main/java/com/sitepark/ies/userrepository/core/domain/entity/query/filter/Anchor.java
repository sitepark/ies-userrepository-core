package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Anchor implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final com.sitepark.ies.userrepository.core.domain.entity.Anchor anchor;

  protected Anchor(
      @JsonProperty("anchor") com.sitepark.ies.userrepository.core.domain.entity.Anchor anchor) {
    Objects.requireNonNull(anchor, "anchor is null");
    this.anchor = anchor;
  }

  public com.sitepark.ies.userrepository.core.domain.entity.Anchor getAnchor() {
    return this.anchor;
  }
}
