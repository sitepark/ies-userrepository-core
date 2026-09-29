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
import org.jspecify.annotations.Nullable;

@JsonDeserialize(builder = UpdatePrivilegeRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpdatePrivilegeRequest {

  private final Privilege privilege;

  private final Updatable<List<Identifier>> roleIdentifiers;

  private UpdatePrivilegeRequest(Builder builder) {
    this.privilege = builder.privilege;
    this.roleIdentifiers =
        builder.roleIdentifiers != null
            ? Updatable.of(List.copyOf(builder.roleIdentifiers))
            : Updatable.unchanged();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Privilege privilege() {
    return this.privilege;
  }

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
    return (o instanceof UpdatePrivilegeRequest that)
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

  @SuppressWarnings("NullAway.Init")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Privilege privilege;
    private @Nullable Set<Identifier> roleIdentifiers;

    private Builder() {}

    private Builder(UpdatePrivilegeRequest request) {
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

    public UpdatePrivilegeRequest build() {
      Objects.requireNonNull(this.privilege, "Privilege must not be null");
      return new UpdatePrivilegeRequest(this);
    }
  }
}
