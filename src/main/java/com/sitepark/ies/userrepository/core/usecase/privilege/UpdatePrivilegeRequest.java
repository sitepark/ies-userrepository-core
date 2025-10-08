package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = UpdatePrivilegeRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpdatePrivilegeRequest {

  @NotNull private final Privilege privilege;

  @NotNull private final List<String> roleIds;

  @Nullable private final String auditParentId;

  private UpdatePrivilegeRequest(Builder builder) {
    this.privilege = builder.privilege;
    this.roleIds = List.copyOf(builder.roleIds);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Privilege privilege() {
    return this.privilege;
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
    return Objects.hash(this.privilege, this.roleIds, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UpdatePrivilegeRequest that)) {
      return false;
    }

    return Objects.equals(this.privilege, that.privilege)
        && Objects.equals(this.roleIds, that.roleIds)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
        + "privilege="
        + privilege
        + ", roleIds="
        + roleIds
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<String> roleIds = new TreeSet<>();
    private Privilege privilege;
    private String auditParentId;

    private Builder() {}

    private Builder(UpdatePrivilegeRequest request) {
      this.privilege = request.privilege;
      this.roleIds.addAll(request.roleIds);
      this.auditParentId = request.auditParentId;
    }

    public Builder privilege(Privilege privilege) {
      this.privilege = privilege;
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

    public UpdatePrivilegeRequest build() {
      Objects.requireNonNull(this.privilege, "Privilege must not be null");
      return new UpdatePrivilegeRequest(this);
    }
  }
}
