package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates user properties indicating whether a user is blocked or has a limited validity
 * period.
 */
@JsonDeserialize(builder = UserValidity.Builder.class)
public class UserValidity {

  private final boolean blocked;

  private final OffsetDateTime validFrom;

  private final OffsetDateTime validTo;

  public static final UserValidity ALWAYS_VALID = UserValidity.builder().blocked(false).build();

  @SuppressWarnings("PMD.LawOfDemeter")
  protected UserValidity(Builder builder) {
    this.blocked = builder.blocked;
    this.validFrom = builder.validFrom;
    this.validTo = builder.validTo;
  }

  public boolean isBlocked() {
    return this.blocked;
  }

  public Optional<OffsetDateTime> getValidFrom() {
    return Optional.ofNullable(this.validFrom);
  }

  public Optional<OffsetDateTime> getValidTo() {
    return Optional.ofNullable(this.validTo);
  }

  @JsonIgnore
  @SuppressWarnings("PMD.SimplifyBooleanReturns")
  public boolean isValid(OffsetDateTime base) {

    Objects.requireNonNull(base, "base is null");

    if (this.blocked || ((this.validFrom != null) && this.validFrom.isAfter(base))) {
      return false;
    }

    return (this.validTo == null) || !this.validTo.isBefore(base);
  }

  @JsonIgnore
  public boolean isNowValid() {
    return this.isValid(OffsetDateTime.now());
  }

  public static Builder builder() {
    return new Builder();
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
    return "UserValidity [blocked="
        + this.blocked
        + ", validFrom="
        + this.validFrom
        + ", validTo="
        + this.validTo
        + "]";
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private boolean blocked;

    private OffsetDateTime validFrom;

    private OffsetDateTime validTo;

    private Builder() {}

    @SuppressWarnings("PMD.LawOfDemeter")
    private Builder(UserValidity userValidity) {
      this.blocked = userValidity.blocked;
      this.validFrom = userValidity.validFrom;
      this.validTo = userValidity.validTo;
    }

    public Builder blocked(boolean blocked) {
      this.blocked = blocked;
      return this;
    }

    public Builder validFrom(OffsetDateTime validFrom) {
      Objects.requireNonNull(validFrom, "validFrom is null");
      this.validFrom = validFrom;
      return this;
    }

    public Builder validTo(OffsetDateTime validTo) {
      Objects.requireNonNull(validTo, "validTo is null");
      this.validTo = validTo;
      return this;
    }

    public UserValidity build() {
      return new UserValidity(this);
    }
  }
}
