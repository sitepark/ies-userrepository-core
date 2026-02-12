package com.sitepark.ies.userrepository.core.usecase.user;

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

@JsonDeserialize(builder = ReassignRolesToUsersRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class ReassignRolesToUsersRequest {

  @NotNull private final List<Identifier> roleIdentifiers;

  @NotNull private final List<Identifier> userIdentifiers;

  private ReassignRolesToUsersRequest(Builder builder) {
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
    this.userIdentifiers = List.copyOf(builder.userIdentifiers);
  }

  public boolean isEmpty() {
    return this.roleIdentifiers.isEmpty() || this.userIdentifiers.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<Identifier> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  public List<Identifier> userIdentifiers() {
    return this.userIdentifiers;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.userIdentifiers, this.roleIdentifiers);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ReassignRolesToUsersRequest that)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers)
        && Objects.equals(this.userIdentifiers, that.userIdentifiers);
  }

  @Override
  public String toString() {
    return "ReassignRolesToUsersRequest{"
        + ", roleIdsIdentifiers="
        + roleIdentifiers
        + "userIdentifiers="
        + userIdentifiers
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<Identifier> roleIdentifiers = new TreeSet<>();
    private final Set<Identifier> userIdentifiers = new TreeSet<>();

    private Builder() {}

    private Builder(ReassignRolesToUsersRequest request) {
      this.roleIdentifiers.addAll(request.userIdentifiers);
      this.userIdentifiers.addAll(request.userIdentifiers);
    }

    public Builder roleIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.roleIdentifiers.clear();
      this.roleIdentifiers.addAll(listBuilder.build());
      return this;
    }

    public Builder userIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.userIdentifiers.clear();
      this.userIdentifiers.addAll(listBuilder.build());
      return this;
    }

    public ReassignRolesToUsersRequest build() {
      return new ReassignRolesToUsersRequest(this);
    }
  }
}
