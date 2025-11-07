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
@JsonDeserialize(builder = UserGrant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserGrant {
  private final boolean create;
  private final boolean read;
  private final boolean write;
  private final boolean delete;
  private final boolean assignRoles;
  private final List<String> allowedRoleIds;

  public static final UserGrant EMPTY = new Builder().build();

  private UserGrant(Builder builder) {
    this.create = builder.create;
    this.read = builder.read;
    this.write = builder.write;
    this.delete = builder.delete;
    this.assignRoles = builder.assignRoles;
    this.allowedRoleIds = List.copyOf(builder.allowedRoleIds);
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
  public boolean assignRoles() {
    return assignRoles;
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public List<String> allowedRoleIds() {
    return this.allowedRoleIds;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return !this.create
        && !this.read
        && !this.write
        && !this.delete
        && !this.assignRoles
        && allowedRoleIds.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserGrant that)) {
      return false;
    }
    return create == that.create
        && read == that.read
        && write == that.write
        && delete == that.delete
        && assignRoles == that.assignRoles
        && Objects.equals(allowedRoleIds, that.allowedRoleIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(create, read, write, delete, assignRoles, allowedRoleIds);
  }

  @Override
  public String toString() {
    return "UserGrant{"
        + "create="
        + create
        + ", read="
        + read
        + ", write="
        + write
        + ", delete="
        + delete
        + ", assignRoles="
        + assignRoles
        + ", allowedRoleIds="
        + allowedRoleIds
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.TooManyMethods")
  public static final class Builder {

    private boolean create = false;
    private boolean read = false;
    private boolean write = false;
    private boolean delete = false;
    private boolean assignRoles = false;
    private final List<String> allowedRoleIds = new ArrayList<>();

    private Builder() {}

    private Builder(UserGrant userGrant) {
      this.create = userGrant.create;
      this.read = userGrant.read;
      this.write = userGrant.write;
      this.delete = userGrant.delete;
      this.assignRoles = userGrant.assignRoles;
      this.allowedRoleIds.addAll(userGrant.allowedRoleIds);
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
    public Builder assignRoles(boolean assignRoles) {
      this.assignRoles = assignRoles;
      return this;
    }

    public Builder allowedRoleIds(Consumer<ListBuilder<String>> configurer) {
      ListBuilder<String> listBuilder = new ListBuilder<>();
      configurer.accept(listBuilder);
      this.allowedRoleIds.clear();
      this.allowedRoleIds.addAll(listBuilder.build());
      return this;
    }

    @JsonSetter
    public Builder allowedRoleIds(List<String> allowedRoleIds) {
      return this.allowedRoleIds(configurer -> configurer.addAll(allowedRoleIds));
    }

    public UserGrant build() {
      return new UserGrant(this);
    }
  }

  @SuppressFBWarnings({
    "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS",
    "EQ_UNUSUAL",
    "HE_EQUALS_USE_HASHCODE"
  })
  public static final class JsonEmptyFilter {
    @Override
    public boolean equals(Object obj) {
      return (obj instanceof UserGrant userGrant) && userGrant.isEmpty();
    }
  }
}
