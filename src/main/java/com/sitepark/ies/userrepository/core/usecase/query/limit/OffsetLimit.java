package com.sitepark.ies.userrepository.core.usecase.query.limit;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class OffsetLimit implements Limit {

  private final int offset;

  private final int limit;

  OffsetLimit(@JsonProperty("offset") int offset, @JsonProperty("limit") int limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset must be >= 0, but was: " + offset);
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be >= 0, but was: " + limit);
    }
    this.offset = offset;
    this.limit = limit;
  }

  public int getOffset() {
    return this.offset;
  }

  public int getLimit() {
    return this.limit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.offset, this.limit);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof OffsetLimit that)) {
      return false;
    }
    return Objects.equals(this.offset, that.offset) && Objects.equals(this.limit, that.limit);
  }

  @Override
  public String toString() {
    return "OffsetLimit [offset=" + offset + ", limit=" + limit + "]";
  }
}
