package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.sharedkernel.base.Updatable;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(builder = CreatePrivilegeRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class CreatePrivilegeRequest {

  @NotNull private final Privilege privilege;

  @NotNull private final Updatable<List<Identifier>> roleIdentifiers;

  private CreatePrivilegeRequest(Builder builder) {
    this.privilege = builder.privilege;
    this.roleIdentifiers =
        builder.roleIdentifiers != null
            ? Updatable.of(List.copyOf(builder.roleIdentifiers))
            : Updatable.unchanged();
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Privilege privilege() {
    return this.privilege;
  }

  @NotNull
  public Updatable<List<Identifier>> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.privilege, this.roleIdentifiers);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CreatePrivilegeRequest that)
        && Objects.equals(this.privilege, that.privilege)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
        + "privilege="
        + privilege
        + ", roleIdentifiers="
        + roleIdentifiers
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Privilege privilege;
    private Set<Identifier> roleIdentifiers;

    private Builder() {}

    private Builder(CreatePrivilegeRequest request) {
      this.privilege = request.privilege;
      if (request.roleIdentifiers.shouldUpdate()) {
        this.roleIdentifiers = new TreeSet<>(request.roleIdentifiers.getValue());
      }
    }

    public Builder privilege(Privilege privilege) {
      this.privilege = privilege;
      return this;
    }

    public Builder roleIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      if (listBuilder.changed()) {
        this.roleIdentifiers = new TreeSet<>();
        this.roleIdentifiers.addAll(listBuilder.build());
      }
      return this;
    }

    public CreatePrivilegeRequest build() {
      Objects.requireNonNull(this.privilege, "Privilege must not be null");
      return new CreatePrivilegeRequest(this);
    }
  }
}
