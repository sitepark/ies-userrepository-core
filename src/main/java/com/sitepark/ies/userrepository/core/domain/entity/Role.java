package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sitepark.ies.userrepository.core.domain.entity.role.RoleDeserializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

/**
 * Defines user roles to manage permissions and access control
 * based on the provided role name, allowing for custom role
 * definitions in the application logic.
 */
@JsonDeserialize(using = RoleDeserializer.class)
public class Role {

  @JsonValue private final String name;

  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  protected Role(String name) {
    Objects.requireNonNull(name, "name is null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("name is blank");
    }
    this.name = name;
  }

  public static Role ofName(String name) {
    return new Role(name);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public final int hashCode() {
    return this.name != null ? this.name.hashCode() : 0;
  }

  @Override
  public final boolean equals(Object o) {

    if (!(o instanceof Role other)) {
      return false;
    }

    return Objects.equals(this.name, other.name);
  }

  @Override
  public String toString() {
    return this.name;
  }
}
