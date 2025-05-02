package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public final class AnchorList implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<com.sitepark.ies.sharedkernel.anchor.domain.Anchor> anchorList;

  AnchorList(
      @JsonProperty("anchorList")
          com.sitepark.ies.sharedkernel.anchor.domain.Anchor... anchorList) {
    Objects.requireNonNull(anchorList, "anchorList is null");
    this.anchorList = List.of(anchorList);
  }

  public List<com.sitepark.ies.sharedkernel.anchor.domain.Anchor> getAnchorList() {
    return this.anchorList;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.anchorList);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof AnchorList that) && Objects.equals(this.anchorList, that.anchorList);
  }

  @Override
  public String toString() {
    return "AnchorList{" + "anchorList=" + anchorList + '}';
  }
}
