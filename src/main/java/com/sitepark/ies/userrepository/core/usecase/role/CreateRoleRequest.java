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
import org.jspecify.annotations.Nullable;

@JsonDeserialize(builder = CreateRoleRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class CreateRoleRequest {

  private final Role role;

  private final Updatable<List<Identifier>> privilegeIdentifiers;

  private CreateRoleRequest(Builder builder) {
    this.role = builder.role;
    this.privilegeIdentifiers =
        builder.privilegeIdentifiers != null
            ? Updatable.of(List.copyOf(builder.privilegeIdentifiers))
            : Updatable.unchanged();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Role role() {
    return this.role;
  }

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
    return (o instanceof CreateRoleRequest that)
        && Objects.equals(this.role, that.role)
        && Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers);
  }

  @Override
  public String toString() {
    return "CreateRoleRequest{"
        + "role="
        + role
        + ", privilegeIdentifiers="
        + privilegeIdentifiers
        + '}';
  }

  @SuppressWarnings("NullAway.Init")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Role role;
    private @Nullable Set<Identifier> privilegeIdentifiers;

    private Builder() {}

    private Builder(CreateRoleRequest request) {
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

    public CreateRoleRequest build() {
      Objects.requireNonNull(this.role, "Role must not be null");
      return new CreateRoleRequest(this);
    }
  }
}
