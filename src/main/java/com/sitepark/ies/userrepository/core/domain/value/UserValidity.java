package com.sitepark.ies.userrepository.core.domain.value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

/**
 * Encapsulates user properties indicating whether a user is blocked or has a limited validity
 * period.
 */
@JsonDeserialize(builder = UserValidity.Builder.class)
@SuppressWarnings("PMD.LawOfDemeter")
@Immutable
public class UserValidity {

  public static final UserValidity ALWAYS_VALID = UserValidity.builder().blocked(false).build();
  private final boolean blocked;
  private final Instant validFrom;
  private final Instant validTo;

  protected UserValidity(Builder builder) {
    this.blocked = builder.blocked;
    this.validFrom = builder.validFrom;
    this.validTo = builder.validTo;
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonProperty
  public boolean blocked() {
    return this.blocked;
  }

  @JsonProperty
  public Instant validFrom() {
    return this.validFrom;
  }

  @JsonProperty
  public Instant validTo() {
    return this.validTo;
  }

  @JsonIgnore
  public boolean isValid(Instant base) {

    Objects.requireNonNull(base, "base is null");

    if (this.blocked || ((this.validFrom != null) && this.validFrom.isAfter(base))) {
      return false;
    }

    return (this.validTo == null) || !this.validTo.isBefore(base);
  }

  @JsonIgnore
  public boolean isNowValid() {
    return this.isValid(Instant.now());
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public final int hashCode() {

    int hash = Boolean.hashCode(this.blocked);
    hash = (this.validFrom != null) ? (31 * hash) + this.validFrom.hashCode() : hash;
    return (this.validTo != null) ? (31 * hash) + this.validTo.hashCode() : hash;
  }

  @Override
  public final boolean equals(Object o) {
    return (o instanceof UserValidity validity)
        && Objects.equals(this.blocked, validity.blocked)
        && Objects.equals(this.validFrom, validity.validFrom)
        && Objects.equals(this.validTo, validity.validTo);
  }

  @Override
  public String toString() {
    return "UserValidity{"
        + "blocked="
        + blocked
        + ", validFrom="
        + validFrom
        + ", validTo="
        + validTo
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private boolean blocked;

    private Instant validFrom;

    private Instant validTo;

    private Builder() {}

    private Builder(UserValidity userValidity) {
      this.blocked = userValidity.blocked;
      this.validFrom = userValidity.validFrom;
      this.validTo = userValidity.validTo;
    }

    public Builder blocked(boolean blocked) {
      this.blocked = blocked;
      return this;
    }

    public Builder validFrom(Instant validFrom) {
      Objects.requireNonNull(validFrom, "validFrom is null");
      this.validFrom = validFrom;
      return this;
    }

    public Builder validTo(Instant validTo) {
      Objects.requireNonNull(validTo, "validTo is null");
      this.validTo = validTo;
      return this;
    }

    public UserValidity build() {
      return new UserValidity(this);
    }
  }
}
