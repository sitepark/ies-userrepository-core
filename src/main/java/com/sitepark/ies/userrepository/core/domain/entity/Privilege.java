package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import java.util.*;

/**
 * Defines user privileges to manage permissions and access control based on the provided privilege
 */
public final class Privilege {

  private final String id;

  private final Anchor anchor;

  private final String name;

  private final String description;

  private final Set<String> roleIds;

  private final Permission permission;

  private Privilege(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.name = builder.name;
    this.description = builder.description;
    this.roleIds = Set.copyOf(builder.roleIds);
    this.permission = builder.permission;
  }

  public static Builder builder() {
    return new Builder();
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

  public Set<String> getRoleIds() {
    return this.roleIds;
  }

  public Permission getPermission() {
    return this.permission;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.id, this.anchor, this.name, this.description, this.roleIds, this.permission);
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof Privilege that)) {
      return false;
    }

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.anchor, that.anchor)
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.description, that.description)
        && Objects.equals(this.roleIds, that.roleIds)
        && Objects.equals(this.permission, that.permission);
  }

  @Override
  public String toString() {
    return "Privilege{"
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
        + ", roleIds='"
        + roleIds
        + '\''
        + ", permission='"
        + permission
        + '}';
  }

  @SuppressWarnings("PMD.TooManyMethods")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<String> roleIds = new HashSet<>();
    private String id;
    private Anchor anchor;
    private String name;
    private String description;
    private Permission permission;

    private Builder() {}

    private Builder(Privilege privilege) {
      this.id = privilege.id;
      this.anchor = privilege.anchor;
      this.name = privilege.name;
      this.description = privilege.description;
      this.roleIds.addAll(privilege.roleIds);
      this.permission = privilege.permission;
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
    public Builder roleIds(Collection<String> roleIds) {
      Objects.requireNonNull(roleIds, "roleIds is null");
      this.roleIds.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleIds(String... roleIds) {
      Objects.requireNonNull(roleIds, "roleIds is null");
      this.roleIds.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleId(String roleId) {
      Objects.requireNonNull(roleId, "roleId is null");
      this.roleIds.add(roleId);
      return this;
    }

    public Builder permission(Permission permission) {
      this.permission = permission;
      return this;
    }

    public Privilege build() {
      if (this.name == null) {
        throw new IllegalStateException("name is not set");
      }
      return new Privilege(this);
    }

    @JsonIgnore
    private String trimToNull(String str) {
      return ((str == null) || str.isBlank()) ? null : str.trim();
    }
  }
}
