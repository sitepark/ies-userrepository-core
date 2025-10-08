package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;
import org.jetbrains.annotations.Nullable;

/**
 * Defines user roles to manage permissions and access control based on the provided role name,
 * allowing for custom role definitions in the application logic.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = Role.Builder.class)
@Immutable
public final class Role {

  @Nullable private final String id;

  @Nullable private final Anchor anchor;

  @Nullable private final String name;

  @Nullable private final String description;

  private Role(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.name = builder.name;
    this.description = builder.description;
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

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.anchor, this.name, this.description);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Role that)
        && Objects.equals(this.id, that.id)
        && Objects.equals(this.anchor, that.anchor)
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.description, that.description);
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
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

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
