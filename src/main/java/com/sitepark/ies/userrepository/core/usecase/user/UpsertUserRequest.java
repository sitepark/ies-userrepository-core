package com.sitepark.ies.userrepository.core.usecase.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = UpsertUserRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class UpsertUserRequest {

  @NotNull private final User user;

  @NotNull private final List<Identifier> roleIdentifiers;

  @Nullable private final String auditParentId;

  private UpsertUserRequest(Builder builder) {
    this.user = builder.user;
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public User user() {
    return this.user;
  }

  @NotNull
  public List<Identifier> roleIdentifiers() {
    return this.roleIdentifiers;
  }

  @Nullable
  public String auditParentId() {
    return this.auditParentId;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.user, this.roleIdentifiers, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof UpsertUserRequest that)
        && Objects.equals(this.user, that.user)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
        + "privilege="
        + user
        + ", roleIdentifiers="
        + roleIdentifiers
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private User user;
    private final Set<Identifier> roleIdentifiers = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(UpsertUserRequest request) {
      this.user = request.user;
      this.roleIdentifiers.addAll(request.roleIdentifiers);
      this.auditParentId = request.auditParentId;
    }

    public Builder user(User user) {
      this.user = user;
      return this;
    }

    public Builder roleIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.roleIdentifiers.clear();
      this.roleIdentifiers.addAll(listBuilder.build());
      return this;
    }

    public UpsertUserRequest build() {
      Objects.requireNonNull(this.user, "User must not be null");
      return new UpsertUserRequest(this);
    }
  }
}
