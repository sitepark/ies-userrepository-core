package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = UpsertRoleRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpsertRoleRequest {

  @NotNull private final Role role;

  @NotNull private final List<String> privilegeIds;

  @Nullable private final String auditParentId;

  private UpsertRoleRequest(Builder builder) {
    this.role = builder.role;
    this.privilegeIds = List.copyOf(builder.privilegeIds);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Role role() {
    return this.role;
  }

  @NotNull
  public List<String> privilegeIds() {
    return this.privilegeIds;
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
    return Objects.hash(this.role, this.privilegeIds, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UpsertRoleRequest that)) {
      return false;
    }

    return Objects.equals(this.role, that.role)
        && Objects.equals(this.privilegeIds, that.privilegeIds)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
        + "privilege="
        + role
        + ", roleIds="
        + privilegeIds
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Role role;
    private final Set<String> privilegeIds = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(UpsertRoleRequest request) {
      this.role = request.role;
      this.privilegeIds.addAll(request.privilegeIds);
      this.auditParentId = request.auditParentId;
    }

    public Builder role(Role role) {
      this.role = role;
      return this;
    }

    public Builder privilegeIds(String... privilegeIds) {
      if (privilegeIds == null) {
        return this;
      }
      this.privilegeIds.clear();
      for (String privilegeId : privilegeIds) {
        this.privilegeId(privilegeId);
      }
      return this;
    }

    public Builder privilegeIds(Collection<String> privilegeIds) {
      if (privilegeIds == null) {
        return this;
      }
      this.privilegeIds.clear();
      for (String privilegeId : privilegeIds) {
        this.privilegeId(privilegeId);
      }
      return this;
    }

    public Builder privilegeId(String privilegeId) {
      if (privilegeId == null || privilegeId.isBlank()) {
        return this;
      }
      this.privilegeIds.add(privilegeId);
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public UpsertRoleRequest build() {
      Objects.requireNonNull(this.role, "Role must not be null");
      return new UpsertRoleRequest(this);
    }
  }
}
