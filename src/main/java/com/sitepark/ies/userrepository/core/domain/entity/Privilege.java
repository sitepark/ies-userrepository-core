package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;

/**
 * Defines user privileges to manage permissions and access control based on the provided privilege
 */
public final class Privilege {

  private final String id;

  private final Anchor anchor;

  private final String name;

  private final String description;

  private Privilege(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.name = builder.name;
    this.description = builder.description;
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

  public static Builder builder() {
    return new Builder();
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

    if (!(o instanceof Privilege that)) {
      return false;
    }

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.anchor, that.anchor)
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.description, that.description);
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
        + '}';
  }

  @SuppressWarnings("PMD.TooManyMethods")
  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private String id;

    private Anchor anchor;

    private String name;

    private String description;

    private Builder() {}

    private Builder(Privilege privilege) {
      this.id = privilege.id;
      this.anchor = privilege.anchor;
      this.name = privilege.name;
      this.description = privilege.description;
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
      assert identifier.getId().isPresent() || identifier.getAnchor().isPresent();
      if (identifier.getAnchor().isPresent()) {
        this.anchor = identifier.getAnchor().get();
        return this;
      }
      this.id = identifier.getId().get();
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
