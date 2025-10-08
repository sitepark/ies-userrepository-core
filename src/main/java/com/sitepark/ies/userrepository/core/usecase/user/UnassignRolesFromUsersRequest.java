package com.sitepark.ies.userrepository.core.usecase.user;

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

@JsonDeserialize(builder = UnassignRolesFromUsersRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class UnassignRolesFromUsersRequest {

  @NotNull private final List<Identifier> roleIdentifiers;

  @NotNull private final List<Identifier> userIdentifiers;

  @Nullable private final String auditParentId;

  private UnassignRolesFromUsersRequest(Builder builder) {
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
    this.userIdentifiers = List.copyOf(builder.userIdentifiers);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public List<Identifier> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  @NotNull
  public List<Identifier> userIdentifiers() {
    return this.userIdentifiers;
  }

  @Nullable
  public String auditParentId() {
    return this.auditParentId;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.roleIdentifiers, this.userIdentifiers, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UnassignRolesFromUsersRequest that)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers)
        && Objects.equals(this.userIdentifiers, that.userIdentifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "UnassignPrivilegesFromRolesRequest{"
        + "roleIdsIdentifiers="
        + roleIdentifiers
        + ", userIdentifiers="
        + userIdentifiers
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.TooManyMethods")
  public static final class Builder {

    private final Set<Identifier> roleIdentifiers = new TreeSet<>();
    private final Set<Identifier> userIdentifiers = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(UnassignRolesFromUsersRequest request) {
      this.roleIdentifiers.addAll(request.roleIdentifiers);
      this.userIdentifiers.addAll(request.userIdentifiers);
      this.auditParentId = request.auditParentId;
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

    public Builder userIdentifiers(Identifier... userIdentifiers) {
      if (userIdentifiers == null) {
        return this;
      }
      this.userIdentifiers.clear();
      for (Identifier userIdentifier : userIdentifiers) {
        this.userIdentifier(userIdentifier);
      }
      return this;
    }

    public Builder userIdentifiers(Collection<Identifier> userIdentifiers) {
      if (userIdentifiers == null) {
        return this;
      }
      this.userIdentifiers.clear();
      for (Identifier userIdentifier : userIdentifiers) {
        this.userIdentifier(userIdentifier);
      }
      return this;
    }

    public Builder userIdentifier(Identifier userIdentifier) {
      if (userIdentifier == null) {
        return this;
      }
      this.userIdentifiers.add(userIdentifier);
      return this;
    }

    public Builder userIds(String... userIds) {
      if (userIds == null) {
        return this;
      }
      this.userIdentifiers.clear();
      for (String userId : userIds) {
        this.userId(userId);
      }
      return this;
    }

    public Builder userIds(Collection<String> userIds) {
      if (userIds == null) {
        return this;
      }
      this.userIdentifiers.clear();
      for (String userId : userIds) {
        this.userId(userId);
      }
      return this;
    }

    public Builder userId(String userId) {
      if (userId == null) {
        return this;
      }
      this.userIdentifiers.add(Identifier.ofId(userId));
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public UnassignRolesFromUsersRequest build() {
      return new UnassignRolesFromUsersRequest(this);
    }
  }
}
