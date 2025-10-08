package com.sitepark.ies.userrepository.core.usecase.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = UpdateUserRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpdateUserRequest {

  @NotNull private final User user;

  @NotNull private final List<String> roleIds;

  @Nullable private final String auditParentId;

  private UpdateUserRequest(Builder builder) {
    this.user = builder.user;
    this.roleIds = List.copyOf(builder.roleIds);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public User user() {
    return this.user;
  }

  @NotNull
  public List<String> roleIds() {
    return this.roleIds;
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
    return Objects.hash(this.user, this.roleIds, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UpdateUserRequest that)
        && Objects.equals(this.user, that.user)
        && Objects.equals(this.roleIds, that.roleIds)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "UpdateUserRequest{"
        + "user="
        + user
        + ", roleIds="
        + roleIds
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private User user;
    private final Set<String> roleIds = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(UpdateUserRequest request) {
      this.user = request.user;
      this.roleIds.addAll(request.roleIds);
      this.auditParentId = request.auditParentId;
    }

    public Builder user(User user) {
      this.user = user;
      return this;
    }

    public Builder roleIds(String... roleIds) {
      if (roleIds == null) {
        return this;
      }
      this.roleIds.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleIds(Collection<String> roleIds) {
      if (roleIds == null) {
        return this;
      }
      this.roleIds.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleId(String roleId) {
      if (roleId == null || roleId.isBlank()) {
        return this;
      }
      this.roleIds.add(roleId);
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public UpdateUserRequest build() {
      Objects.requireNonNull(this.user, "user must not be null");
      return new UpdateUserRequest(this);
    }
  }
}
