package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.userrepository.core.usecase.user.AssignRolesToUsersRequest.Builder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
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

  public boolean isEmpty() {
    return this.roleIdentifiers.isEmpty() || this.privilegeIdentifiers.isEmpty();
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
    return (o instanceof AssignPrivilegesToRolesRequest that)
        && Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers)
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

    public Builder roleIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.roleIdentifiers.clear();
      this.roleIdentifiers.addAll(listBuilder.build());
      return this;
    }

    public Builder privilegeIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.privilegeIdentifiers.clear();
      this.privilegeIdentifiers.addAll(listBuilder.build());
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
