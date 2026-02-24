package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.sharedkernel.base.Updatable;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(builder = UpsertRoleRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpsertRoleRequest {

  @NotNull private final Role role;

  @NotNull private final Updatable<List<Identifier>> privilegeIdentifiers;

  private UpsertRoleRequest(Builder builder) {
    this.role = builder.role;
    this.privilegeIdentifiers =
        builder.privilegeIdentifiers != null
            ? Updatable.of(List.copyOf(builder.privilegeIdentifiers))
            : Updatable.unchanged();
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Role role() {
    return this.role;
  }

  @NotNull
  public Updatable<List<Identifier>> privilegeIdentifiers() {
    return this.privilegeIdentifiers;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.role, this.privilegeIdentifiers);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UpsertRoleRequest that)
        && Objects.equals(this.role, that.role)
        && Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers);
  }

  @Override
  public String toString() {
    return "UpsertRoleRequest{"
        + "role="
        + role
        + ", privilegeIdentifiers="
        + privilegeIdentifiers
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Role role;
    private Set<Identifier> privilegeIdentifiers;

    private Builder() {}

    private Builder(UpsertRoleRequest request) {
      this.role = request.role;
      if (request.privilegeIdentifiers.shouldUpdate()) {
        this.privilegeIdentifiers = new TreeSet<>(request.privilegeIdentifiers.getValue());
      }
    }

    public Builder role(Role role) {
      this.role = role;
      return this;
    }

    public Builder privilegeIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      if (listBuilder.changed()) {
        this.privilegeIdentifiers = new TreeSet<>();
        this.privilegeIdentifiers.addAll(listBuilder.build());
      }
      return this;
    }

    public UpsertRoleRequest build() {
      Objects.requireNonNull(this.role, "Role must not be null");
      return new UpsertRoleRequest(this);
    }
  }
}
