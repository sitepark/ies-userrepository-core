package com.sitepark.ies.userrepository.core.domain.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = Contact.Builder.class)
@Immutable
public final class Contact {

  @Nullable private final String phonePrivate;

  @Nullable private final String phoneOffice;

  private Contact(Builder builder) {
    this.phonePrivate = builder.phonePrivate;
    this.phoneOffice = builder.phoneOffice;
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonProperty("phonePrivate")
  public String phonePrivate() {
    return phonePrivate;
  }

  @JsonProperty("phoneOffice")
  public String phoneOffice() {
    return phoneOffice;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Contact that)) {
      return false;
    }
    return Objects.equals(phonePrivate, that.phonePrivate)
        && Objects.equals(phoneOffice, that.phoneOffice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(phonePrivate, phoneOffice);
  }

  @Override
  public String toString() {
    return "Contact{"
        + "phonePrivate='"
        + phonePrivate
        + '\''
        + ", phoneOffice='"
        + phoneOffice
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    @Nullable private String phonePrivate;
    @Nullable private String phoneOffice;

    private Builder() {}

    public Builder phonePrivate(@Nullable String phonePrivate) {
      this.phonePrivate = phonePrivate;
      return this;
    }

    public Builder phoneOffice(@Nullable String phoneOffice) {
      this.phoneOffice = phoneOffice;
      return this;
    }

    public Contact build() {
      return new Contact(this);
    }
  }
}
