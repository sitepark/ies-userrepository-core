package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.Nullable;

/**
 * Defines user roles to manage permissions and access control based on the provided role name,
 * allowing for custom role definitions in the application logic.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = Role.Builder.class)
public final class Role {

  @Nullable private final String id;

  @Nullable private final Anchor anchor;

  @Nullable private final String name;

  @Nullable private final String description;

  private final List<String> privilegeIds;

  private Role(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.name = builder.name;
    this.description = builder.description;
    this.privilegeIds = List.copyOf(builder.privilegeIds);
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonProperty
  public String id() {
    return this.id;
  }

  @JsonProperty
  public Anchor anchor() {
    return this.anchor;
  }

  @JsonProperty
  public String name() {
    return this.name;
  }

  @JsonProperty
  public String description() {
    return this.description;
  }

  @JsonProperty
  public List<String> privilegeIds() {
    return this.privilegeIds;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.anchor, this.name, this.description, this.privilegeIds);
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof Role that)) {
      return false;
    }

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.anchor, that.anchor)
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.description, that.description)
        && Objects.equals(this.privilegeIds, that.privilegeIds);
  }

  @Override
  public String toString() {
    return "Role{"
        + "id='"
        + id
        + '\''
        + ", anchor="
        + anchor
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", privilegeIds="
        + privilegeIds
        + '}';
  }

  @SuppressWarnings("PMD.TooManyMethods")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<String> privilegeIds = new TreeSet<>();
    private String id;
    private Anchor anchor;
    private String name;
    private String description;

    private Builder() {}

    private Builder(Role role) {
      this.id = role.id;
      this.anchor = role.anchor;
      this.name = role.name;
      this.description = role.description;
      this.privilegeIds.addAll(role.privilegeIds);
    }

    public Builder id(String id) {
      Objects.requireNonNull(id, "id is null");
      if (!Identifier.isId(id)) {
        throw new IllegalArgumentException(id + " is not an id");
      }
      this.id = id;
      return this;
    }

    public Builder identifier(Identifier identifier) {
      assert identifier.getId() != null || identifier.getAnchor() != null;
      if (identifier.getAnchor() != null) {
        this.anchor = identifier.getAnchor();
        return this;
      }
      this.id = identifier.getId();
      return this;
    }

    public Builder anchor(String anchor) {
      this.anchor = Anchor.ofString(anchor);
      return this;
    }

    public Builder anchor(Anchor anchor) {
      this.anchor = anchor;
      return this;
    }

    public Builder name(String name) {
      this.name = this.trimToNull(name);
      return this;
    }

    public Builder description(String description) {
      this.description = this.trimToNull(description);
      return this;
    }

    @JsonSetter
    public Builder privilegeIds(String... privilegeIds) {
      Objects.requireNonNull(privilegeIds, "privilegeIds is null");
      this.privilegeIds.clear();
      for (String privilege : privilegeIds) {
        this.privilegeId(privilege);
      }
      return this;
    }

    public Builder privilegeIds(Collection<String> privilegeIds) {
      Objects.requireNonNull(privilegeIds, "privilegeIds is null");
      this.privilegeIds.clear();
      for (String privilegeId : privilegeIds) {
        this.privilegeId(privilegeId);
      }
      return this;
    }

    public Builder privilegeId(String privilegeId) {
      Objects.requireNonNull(privilegeId, "privilegeId is null");
      this.privilegeIds.add(privilegeId);
      return this;
    }

    public Role build() {
      if (this.name == null) {
        throw new IllegalStateException("name is not set");
      }
      return new Role(this);
    }

    @JsonIgnore
    private String trimToNull(String str) {
      return ((str == null) || str.isBlank()) ? null : str.trim();
    }
  }
}
