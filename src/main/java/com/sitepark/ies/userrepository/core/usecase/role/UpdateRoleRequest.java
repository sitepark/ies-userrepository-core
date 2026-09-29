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

@JsonDeserialize(builder = UpdateRoleRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpdateRoleRequest {

  private final Role role;

  private final Updatable<List<Identifier>> privilegeIdentifiers;

  private UpdateRoleRequest(Builder builder) {
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
    return (o instanceof UpdateRoleRequest that)
        && Objects.equals(this.role, that.role)
        && Objects.equals(this.privilegeIdentifiers, that.privilegeIdentifiers);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
        + "role="
        + role
        + ", privilegeIdentifiers="
        + privilegeIdentifiers
        + '}';
  }

  @SuppressWarnings("NullAway.Init")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private @Nullable Set<Identifier> privilegeIdentifiers;
    private Role role;

    private Builder() {}

    private Builder(UpdateRoleRequest request) {
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

    public UpdateRoleRequest build() {
      Objects.requireNonNull(this.role, "role must not be null");
      return new UpdateRoleRequest(this);
    }
  }
}
