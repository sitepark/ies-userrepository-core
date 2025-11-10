package com.sitepark.ies.userrepository.core.domain.value.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.ListBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.concurrent.Immutable;

@Immutable
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.TooManyMethods"})
@JsonDeserialize(builder = RoleGrant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class RoleGrant {
  private final boolean create;
  private final boolean read;
  private final boolean write;
  private final boolean delete;
  private final boolean assignPrivileges;
  private final List<String> allowedPrivilegeIds;

  public static final RoleGrant EMPTY = new Builder().build();

  private RoleGrant(Builder builder) {
    this.create = builder.create;
    this.read = builder.read;
    this.write = builder.write;
    this.delete = builder.delete;
    this.assignPrivileges = builder.assignPrivileges;
    this.allowedPrivilegeIds = List.copyOf(builder.allowedPrivilegeIds);
  }

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

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  public boolean assignPrivileges() {
    return assignPrivileges;
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public List<String> allowedPrivilegeIds() {
    return this.allowedPrivilegeIds;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return !this.create
        && !this.read
        && !this.write
        && !this.delete
        && !this.assignPrivileges
        && allowedPrivilegeIds.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RoleGrant that)) {
      return false;
    }
    return create == that.create
        && read == that.read
        && write == that.write
        && delete == that.delete
        && assignPrivileges == that.assignPrivileges
        && Objects.equals(allowedPrivilegeIds, that.allowedPrivilegeIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(create, read, write, delete, assignPrivileges, allowedPrivilegeIds);
  }

  @Override
  public String toString() {
    return "RoleGrant{"
        + "create="
        + create
        + ", read="
        + read
        + ", write="
        + write
        + ", delete="
        + delete
        + ", assignPrivileges="
        + assignPrivileges
        + ", allowedPrivilegeIds="
        + allowedPrivilegeIds
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private boolean create;
    private boolean read;
    private boolean write;
    private boolean delete;
    private boolean assignPrivileges;
    private final List<String> allowedPrivilegeIds = new ArrayList<>();

    private Builder() {}

    private Builder(RoleGrant roleGrant) {
      this.create = roleGrant.create;
      this.read = roleGrant.read;
      this.write = roleGrant.write;
      this.delete = roleGrant.delete;
      this.assignPrivileges = roleGrant.assignPrivileges;
      this.allowedPrivilegeIds.addAll(roleGrant.allowedPrivilegeIds);
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

    @JsonSetter
    public Builder assignPrivileges(boolean assignPrivileges) {
      this.assignPrivileges = assignPrivileges;
      return this;
    }

    public Builder allowedPrivilegeIds(Consumer<ListBuilder<String>> configurer) {
      ListBuilder<String> listBuilder = new ListBuilder<>();
      configurer.accept(listBuilder);
      this.allowedPrivilegeIds.clear();
      this.allowedPrivilegeIds.addAll(listBuilder.build());
      return this;
    }

    @JsonSetter
    public Builder allowedPrivilegeIds(List<String> allowedPrivilegeIds) {
      return this.allowedPrivilegeIds(configurer -> configurer.addAll(allowedPrivilegeIds));
    }

    public RoleGrant build() {
      return new RoleGrant(this);
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
      return (obj instanceof RoleGrant roleGrant) && roleGrant.isEmpty();
    }
  }
}
