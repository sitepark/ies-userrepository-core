package com.sitepark.ies.userrepository.core.domain.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = Address.Builder.class)
public final class Address {

  @Nullable private final String street;

  @Nullable private final String houseNumber;

  @Nullable private final String postalCode;

  @Nullable private final String city;

  private Address(Builder builder) {
    this.street = builder.street;
    this.houseNumber = builder.houseNumber;
    this.postalCode = builder.postalCode;
    this.city = builder.city;
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonProperty("street")
  public String street() {
    return street;
  }

  @JsonProperty("houseNumber")
  public String houseNumber() {
    return houseNumber;
  }

  @JsonProperty("postalCode")
  public String postalCode() {
    return postalCode;
  }

  @JsonProperty("city")
  public String city() {
    return city;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Address that)) {
      return false;
    }
    return Objects.equals(street, that.street)
        && Objects.equals(houseNumber, that.houseNumber)
        && Objects.equals(postalCode, that.postalCode)
        && Objects.equals(city, that.city);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, houseNumber, postalCode, city);
  }

  @Override
  public String toString() {
    return "Address{"
        + "street='"
        + street
        + '\''
        + ", houseNumber='"
        + houseNumber
        + '\''
        + ", postalCode='"
        + postalCode
        + '\''
        + ", city='"
        + city
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    @Nullable private String street;
    @Nullable private String houseNumber;
    @Nullable private String postalCode;
    @Nullable private String city;

    private Builder() {}

    public Builder street(@Nullable String street) {
      this.street = street;
      return this;
    }

    public Builder houseNumber(@Nullable String houseNumber) {
      this.houseNumber = houseNumber;
      return this;
    }

    public Builder postalCode(@Nullable String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public Builder city(@Nullable String city) {
      this.city = city;
      return this;
    }

    public Address build() {
      return new Address(this);
    }
  }
}
