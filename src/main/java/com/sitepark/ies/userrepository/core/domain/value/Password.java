package com.sitepark.ies.userrepository.core.domain.value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@JsonDeserialize(builder = Password.Builder.class)
@Immutable
public final class Password {

  private final String hashAlgorithm;

  private final String hash;

  private final String clearText;

  private Password(Builder builder) {
    this.hashAlgorithm = builder.hashAlgorithm;
    this.hash = builder.hash;
    this.clearText = builder.clearText;
  }

  @JsonIgnore
  public String getHashAlgorithm() {
    return hashAlgorithm;
  }

  @JsonIgnore
  public String getHash() {
    return hash;
  }

  public String getClearText() {
    return clearText;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Password that)) {
      return false;
    }
    return Objects.equals(hashAlgorithm, that.hashAlgorithm)
        && Objects.equals(hash, that.hash)
        && Objects.equals(clearText, that.clearText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hashAlgorithm, hash, clearText);
  }

  @Override
  public String toString() {
    return "Password{"
        + "hashAlgorithm='"
        + hashAlgorithm
        + '\''
        + ", hash='"
        + (hash != null ? "******" : null)
        + '\''
        + ", clearText='"
        + (clearText != null ? "******" : null)
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private String hashAlgorithm;
    private String hash;
    private String clearText;

    private Builder() {}

    private Builder(Password password) {
      this.hashAlgorithm = password.hashAlgorithm;
      this.hash = password.hash;
      this.clearText = password.clearText;
    }

    public Builder hashAlgorithm(String hashAlgorithm) {
      this.hashAlgorithm = hashAlgorithm;
      return this;
    }

    public Builder hash(String hash) {
      this.hash = hash;
      return this;
    }

    public Builder clearText(String clearText) {
      this.clearText = clearText;
      return this;
    }

    public Password build() {
      return new Password(this);
    }
  }
}
