package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Defines user roles to manage permissions and access control based on the provided role name,
 * allowing for custom role definitions in the application logic.
 */
public final class Role {

  private final String id;

  private final Anchor anchor;

  private final String name;

  private final String description;

  private final List<Identifier> privileges;

  protected Role(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.name = builder.name;
    this.description = builder.description;
    this.privileges = Collections.unmodifiableList(builder.privileges);
  }

  public String getId() {
    return this.id;
  }

  public Anchor getAnchor() {
    return this.anchor;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public List<Identifier> getPrivileges() {
    return this.privileges;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.anchor, this.name, this.description, this.privileges);
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
        && Objects.equals(this.privileges, that.privileges);
  }

  @Override
  public String toString() {
    return "Role [id="
        + id
        + ", anchor="
        + anchor
        + ", name="
        + name
        + ", description="
        + description
        + ", privileges="
        + privileges
        + "]";
  }

  public static Builder builder(Role role) {
    return new Builder(role);
  }

  @SuppressWarnings("PMD.TooManyMethods")
  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private String id;

    private Anchor anchor;

    private String name;

    private String description;

    private final List<Identifier> privileges = new ArrayList<>();

    protected Builder() {}

    protected Builder(Role role) {
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
      if (identifier.getId().isPresent()) {
        this.id = identifier.getId().get();
        return this;
      }
      this.anchor = identifier.getAnchor().get();
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
    public Builder privileges(Identifier... privileges) {
      Objects.requireNonNull(privileges, "privileges is null");
      this.privileges.clear();
      for (Identifier privilege : privileges) {
        this.privilege(privilege);
      }
      return this;
    }

    public Builder privileges(List<Identifier> privileges) {
      Objects.requireNonNull(privileges, "privileges is null");
      this.privileges.clear();
      for (Identifier role : privileges) {
        this.privilege(role);
      }
      return this;
    }

    public Builder privilege(Identifier privilege) {
      Objects.requireNonNull(privilege, "privilege is null");
      this.privileges.add(privilege);
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
