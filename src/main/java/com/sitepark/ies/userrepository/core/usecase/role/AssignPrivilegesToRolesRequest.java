package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = AssignPrivilegesToRolesRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class AssignPrivilegesToRolesRequest {

  @NotNull private final List<Identifier> privilegeIdentifiers;

  @NotNull private final List<Identifier> roleIdentifiers;

  @Nullable private final String auditParentId;

  private AssignPrivilegesToRolesRequest(Builder builder) {
    this.privilegeIdentifiers = List.copyOf(builder.privilegeIdentifiers);
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<Identifier> privilegeIdentifiers() {
    return this.privilegeIdentifiers;
  }

  public List<Identifier> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  public String auditParentId() {
    return this.auditParentId;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.privilegeIdentifiers, this.roleIdentifiers, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AssignPrivilegesToRolesRequest that)) {
      return false;
    }

    return Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "AssignPrivilegesToRolesRequest{"
        + "privilegeIdentifiers="
        + privilegeIdentifiers
        + ", roleIdsIdentifiers="
        + roleIdentifiers
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.TooManyMethods")
  public static final class Builder {

    private final Set<Identifier> privilegeIdentifiers = new TreeSet<>();
    private final Set<Identifier> roleIdentifiers = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(AssignPrivilegesToRolesRequest request) {
      this.privilegeIdentifiers.addAll(request.privilegeIdentifiers);
      this.roleIdentifiers.addAll(request.privilegeIdentifiers);
      this.auditParentId = request.auditParentId;
    }

    public Builder privilegeIdentifiers(Identifier... privilegeIdentifiers) {
      if (privilegeIdentifiers == null) {
        return this;
      }
      this.privilegeIdentifiers.clear();
      for (Identifier privilegeIdentifier : privilegeIdentifiers) {
        this.privilegeIdentifier(privilegeIdentifier);
      }
      return this;
    }

    public Builder privilegeIdentifiers(Collection<Identifier> privilegeIdentifiers) {
      if (privilegeIdentifiers == null) {
        return this;
      }
      this.privilegeIdentifiers.clear();
      for (Identifier privilegeIdentifier : privilegeIdentifiers) {
        this.privilegeIdentifier(privilegeIdentifier);
      }
      return this;
    }

    public Builder privilegeIdentifier(Identifier privilegeIdentifier) {
      if (privilegeIdentifier == null) {
        return this;
      }
      this.privilegeIdentifiers.add(privilegeIdentifier);
      return this;
    }

    public Builder privilegeIds(String... privilegeIds) {
      if (privilegeIds == null) {
        return this;
      }
      this.privilegeIdentifiers.clear();
      for (String privilegeId : privilegeIds) {
        this.privilegeId(privilegeId);
      }
      return this;
    }

    public Builder privilegeIds(Collection<String> privilegeIds) {
      if (privilegeIds == null) {
        return this;
      }
      this.privilegeIdentifiers.clear();
      for (String privilegeId : privilegeIds) {
        this.privilegeId(privilegeId);
      }
      return this;
    }

    public Builder privilegeId(String privilegeId) {
      if (privilegeId == null) {
        return this;
      }
      this.privilegeIdentifiers.add(Identifier.ofId(privilegeId));
      return this;
    }

    public Builder roleIdentifiers(Identifier... roleIdentifiers) {
      if (roleIdentifiers == null) {
        return this;
      }
      this.roleIdentifiers.clear();
      for (Identifier roleIdentifier : roleIdentifiers) {
        this.roleIdentifier(roleIdentifier);
      }
      return this;
    }

    public Builder roleIdentifiers(Collection<Identifier> roleIdentifiers) {
      if (roleIdentifiers == null) {
        return this;
      }
      this.roleIdentifiers.clear();
      for (Identifier roleIdentifier : roleIdentifiers) {
        this.roleIdentifier(roleIdentifier);
      }
      return this;
    }

    public Builder roleIdentifier(Identifier roleIdentifier) {
      if (roleIdentifier == null) {
        return this;
      }
      this.roleIdentifiers.add(roleIdentifier);
      return this;
    }

    public Builder roleIds(String... roleIds) {
      if (roleIds == null) {
        return this;
      }
      this.roleIdentifiers.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleIds(Collection<String> roleIds) {
      if (roleIds == null) {
        return this;
      }
      this.roleIdentifiers.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleId(String roleId) {
      if (roleId == null || roleId.isBlank()) {
        return this;
      }
      this.roleIdentifiers.add(Identifier.ofId(roleId));
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public AssignPrivilegesToRolesRequest build() {
      return new AssignPrivilegesToRolesRequest(this);
    }
  }
}
