package com.sitepark.ies.userrepository.core.usecase.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.sharedkernel.base.Updatable;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

@JsonDeserialize(builder = UpdateUserRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpdateUserRequest {

  @NotNull private final User user;

  @NotNull private final Updatable<List<Identifier>> roleIdentifiers;

  private UpdateUserRequest(Builder builder) {
    this.user = builder.user;
    this.roleIdentifiers =
        builder.roleIdentifiers != null
            ? Updatable.of(List.copyOf(builder.roleIdentifiers))
            : Updatable.unchanged();
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public User user() {
    return this.user;
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
    return Objects.hash(this.user, this.roleIdentifiers);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UpdateUserRequest that)
        && Objects.equals(this.user, that.user)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers);
  }

  @Override
  public String toString() {
    return "UpdateUserRequest{" + "user=" + user + ", roleIdentifiers=" + roleIdentifiers + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private User user;
    private Set<Identifier> roleIdentifiers;

    private Builder() {}

    private Builder(UpdateUserRequest request) {
      this.user = request.user;
      if (request.roleIdentifiers.shouldUpdate()) {
        this.roleIdentifiers = new TreeSet<>(request.roleIdentifiers.getValue());
      }
    }

    public Builder user(User user) {
      this.user = user;
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

    public UpdateUserRequest build() {
      Objects.requireNonNull(this.user, "user must not be null");
      return new UpdateUserRequest(this);
    }
  }
}
