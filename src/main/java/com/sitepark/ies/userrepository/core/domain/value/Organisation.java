package com.sitepark.ies.userrepository.core.domain.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = Organisation.Builder.class)
@Immutable
public final class Organisation {

  @Nullable private final String name;

  private Organisation(Builder builder) {
    this.name = builder.name;
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonProperty("name")
  public String name() {
    return name;
  }

  @Override
  @SuppressWarnings("PMD.SimplifyBooleanReturns")
  public boolean equals(Object obj) {
    if (!(obj instanceof Organisation that)) {
      return false;
    }
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Organisation{" + "name='" + name + '\'' + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    @Nullable private String name;

    private Builder() {}

    public Builder name(@Nullable String name) {
      this.name = name;
      return this;
    }

    public Organisation build() {
      return new Organisation(this);
    }
  }
}
