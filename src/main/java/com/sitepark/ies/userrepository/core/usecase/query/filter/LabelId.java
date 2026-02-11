package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.ies.sharedkernel.json.UniquePropertyType;
import java.util.Objects;

@UniquePropertyType(uniqueProperty = "labelid")
public final class LabelId implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String labelId;

  LabelId(@JsonProperty("labelId") String labelId) {
    Objects.requireNonNull(labelId, "labelId is null");
    this.labelId = labelId;
  }

  public String getLabelId() {
    return this.labelId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.labelId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof LabelId that) && Objects.equals(this.labelId, that.labelId);
  }

  @Override
  public String toString() {
    return "LabelId{" + "labelId='" + labelId + '\'' + '}';
  }
}
