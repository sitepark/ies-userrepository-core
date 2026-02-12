package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.userrepository.core.usecase.privilege.ReassignRolesToPrivilegesRequest.Builder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(builder = ReassignRolesToPrivilegesRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class ReassignRolesToPrivilegesRequest {

  @NotNull private final List<Identifier> roleIdentifiers;

  @NotNull private final List<Identifier> privilegeIdentifiers;

  private ReassignRolesToPrivilegesRequest(Builder builder) {
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
    this.privilegeIdentifiers = List.copyOf(builder.privilegeIdentifiers);
  }

  public boolean isEmpty() {
    return this.roleIdentifiers.isEmpty() || this.privilegeIdentifiers.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<Identifier> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  public List<Identifier> privilegeIdentifiers() {
    return this.privilegeIdentifiers;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.roleIdentifiers, this.privilegeIdentifiers);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ReassignRolesToPrivilegesRequest that)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers)
        && Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers);
  }

  @Override
  public String toString() {
    return "ReassignRolesToPrivilegesRequest{"
        + "roleIdentifiers="
        + roleIdentifiers
        + ", privilegeIdentifiers="
        + privilegeIdentifiers
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<Identifier> roleIdentifiers = new TreeSet<>();
    private final Set<Identifier> privilegeIdentifiers = new TreeSet<>();

    private Builder() {}

    private Builder(ReassignRolesToPrivilegesRequest request) {
      this.roleIdentifiers.addAll(request.roleIdentifiers);
      this.privilegeIdentifiers.addAll(request.privilegeIdentifiers);
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

    public ReassignRolesToPrivilegesRequest build() {
      return new ReassignRolesToPrivilegesRequest(this);
    }
  }
}
