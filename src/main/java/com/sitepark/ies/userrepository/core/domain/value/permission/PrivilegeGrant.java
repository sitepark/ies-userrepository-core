package com.sitepark.ies.userrepository.core.domain.value.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserGrant.Builder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = PrivilegeGrant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class PrivilegeGrant {
  private final boolean create;
  private final boolean read;
  private final boolean write;
  private final boolean delete;

  private PrivilegeGrant(Builder builder) {
    this.create = builder.create;
    this.read = builder.read;
    this.write = builder.write;
    this.delete = builder.delete;
  }

  public static final PrivilegeGrant EMPTY = new Builder().build();

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  public boolean create() {
    return create;
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  public boolean read() {
    return read;
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  public boolean write() {
    return write;
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  public boolean delete() {
    return delete;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return !this.create && !this.read && !this.write && !this.delete;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PrivilegeGrant that)) {
      return false;
    }
    return create == that.create
        && read == that.read
        && write == that.write
        && delete == that.delete;
  }

  @Override
  public int hashCode() {
    return Objects.hash(create, read, write, delete);
  }

  @Override
  public String toString() {
    return "PrivilegeGrant{"
        + "create="
        + create
        + ", read="
        + read
        + ", write="
        + write
        + ", delete="
        + delete
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private boolean create;
    private boolean read;
    private boolean write;
    private boolean delete;

    private Builder() {}

    private Builder(PrivilegeGrant roleGrant) {
      this.create = roleGrant.create;
      this.read = roleGrant.read;
      this.write = roleGrant.write;
      this.delete = roleGrant.delete;
    }

    @JsonSetter
    public Builder create(boolean create) {
      this.create = create;
      return this;
    }

    @JsonSetter
    public Builder read(boolean read) {
      this.read = read;
      return this;
    }

    @JsonSetter
    public Builder write(boolean write) {
      this.write = write;
      return this;
    }

    @JsonSetter
    public Builder delete(boolean delete) {
      this.delete = delete;
      return this;
    }

    public PrivilegeGrant build() {
      return new PrivilegeGrant(this);
    }
  }

  @SuppressFBWarnings({
    "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS",
    "EQ_UNUSUAL",
    "HE_EQUALS_USE_HASHCODE"
  })
  @SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
  public static final class JsonEmptyFilter {
    @Override
    public boolean equals(Object obj) {
      return (obj instanceof PrivilegeGrant privilegeGrant) && privilegeGrant.isEmpty();
    }
  }
}
