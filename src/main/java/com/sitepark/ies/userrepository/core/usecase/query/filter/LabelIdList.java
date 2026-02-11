package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.ies.sharedkernel.json.UniquePropertyType;
import java.util.List;
import java.util.Objects;

@UniquePropertyType(uniqueProperty = "labelidlist")
public final class LabelIdList implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final List<String> labelIdList;

  LabelIdList(@JsonProperty("labelIdList") String... labelIdList) {
    Objects.requireNonNull(labelIdList, "labelIdList is null");
    this.labelIdList = List.of(labelIdList);
  }

  public List<String> getLabelIdList() {
    return List.copyOf(this.labelIdList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.labelIdList);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof LabelIdList that) && Objects.equals(this.labelIdList, that.labelIdList);
  }

  @Override
  public String toString() {
    return "LabelIdList{" + "labelIdList=" + labelIdList + '}';
  }
}
