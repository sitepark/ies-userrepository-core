package com.sitepark.ies.userrepository.core.domain.value.permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.security.Permission;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize(builder = UserManagementPermission.Builder.class)
@JsonPropertyOrder({"type", "userGrant", "roleGrant", "privilegeGrant"})
public class UserManagementPermission implements Permission {

  public static final String TYPE = "USER_MANAGEMENT";

  private final UserGrant userGrant;
  private final RoleGrant roleGrant;
  private final PrivilegeGrant privilegeGrant;

  public static final UserManagementPermission EMPTY = new Builder().build();

  private UserManagementPermission(Builder builder) {
    this.userGrant = Objects.requireNonNullElse(builder.userGrant, UserGrant.EMPTY);
    this.roleGrant = Objects.requireNonNullElse(builder.roleGrant, RoleGrant.EMPTY);
    this.privilegeGrant = Objects.requireNonNullElse(builder.privilegeGrant, PrivilegeGrant.EMPTY);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @JsonProperty
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = UserGrant.JsonEmptyFilter.class)
  public UserGrant userGrant() {
    return userGrant;
  }

  @JsonProperty
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RoleGrant.JsonEmptyFilter.class)
  public RoleGrant roleGrant() {
    return roleGrant;
  }

  @JsonProperty
  @JsonInclude(
      value = JsonInclude.Include.CUSTOM,
      valueFilter = PrivilegeGrant.JsonEmptyFilter.class)
  public PrivilegeGrant privilegeGrant() {
    return privilegeGrant;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserManagementPermission that)) {
      return false;
    }
    return Objects.equals(userGrant, that.userGrant)
        && Objects.equals(roleGrant, that.roleGrant)
        && Objects.equals(privilegeGrant, that.privilegeGrant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userGrant, roleGrant, privilegeGrant);
  }

  @Override
  public String toString() {
    return "UserManagementPermission{"
        + "userGrant="
        + userGrant
        + ", roleGrant="
        + roleGrant
        + ", privilegeGrant="
        + privilegeGrant
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties({"type"})
  @SuppressWarnings("PMD.TooManyMethods")
  public static final class Builder {
    private UserGrant userGrant;
    private RoleGrant roleGrant;
    private PrivilegeGrant privilegeGrant;

    private Builder() {}

    private Builder(UserManagementPermission userManagementPermission) {
      this.userGrant = userManagementPermission.userGrant;
      this.roleGrant = userManagementPermission.roleGrant;
      this.privilegeGrant = userManagementPermission.privilegeGrant;
    }

    public Builder userGrant(UserGrant userGrant) {
      this.userGrant = userGrant;
      return this;
    }

    public Builder roleGrant(RoleGrant roleGrant) {
      this.roleGrant = roleGrant;
      return this;
    }

    public Builder privilegeGrant(PrivilegeGrant privilegeGrant) {
      this.privilegeGrant = privilegeGrant;
      return this;
    }

    public UserManagementPermission build() {
      return new UserManagementPermission(this);
    }
  }
}
