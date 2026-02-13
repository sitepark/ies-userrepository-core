package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(builder = UnassignPrivilegesFromRolesRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class UnassignPrivilegesFromRolesRequest {

  @NotNull private final List<Identifier> privilegeIdentifiers;

  @NotNull private final List<Identifier> roleIdentifiers;

  private UnassignPrivilegesFromRolesRequest(Builder builder) {
    this.privilegeIdentifiers = List.copyOf(builder.privilegeIdentifiers);
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
  }

  public static Builder builder() {
    return new Builder();
  }

  public boolean isEmpty() {
    return this.roleIdentifiers.isEmpty() || this.privilegeIdentifiers.isEmpty();
  }

  @NotNull
  public List<Identifier> privilegeIdentifiers() {
    return this.privilegeIdentifiers;
  }

  @NotNull
  public List<Identifier> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.privilegeIdentifiers, this.roleIdentifiers);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UnassignPrivilegesFromRolesRequest that)
        && Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers);
  }

  @Override
  public String toString() {
    return "UnassignPrivilegesFromRolesRequest{"
        + "privilegeIdentifiers="
        + privilegeIdentifiers
        + ", roleIdentifiers="
        + roleIdentifiers
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<Identifier> privilegeIdentifiers = new TreeSet<>();
    private final Set<Identifier> roleIdentifiers = new TreeSet<>();

    private Builder() {}

    private Builder(UnassignPrivilegesFromRolesRequest request) {
      this.privilegeIdentifiers.addAll(request.privilegeIdentifiers);
      this.roleIdentifiers.addAll(request.roleIdentifiers);
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

    public UnassignPrivilegesFromRolesRequest build() {
      return new UnassignPrivilegesFromRolesRequest(this);
    }
  }
}
