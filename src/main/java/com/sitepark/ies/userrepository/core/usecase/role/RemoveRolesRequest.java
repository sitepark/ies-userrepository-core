package com.sitepark.ies.userrepository.core.usecase.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.userrepository.core.usecase.role.CreateRoleRequest.Builder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = RemoveRolesRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class RemoveRolesRequest {

  @NotNull private final List<Identifier> identifiers;

  @Nullable private final String auditParentId;

  private RemoveRolesRequest(Builder builder) {
    this.identifiers = List.copyOf(builder.identifiers);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public List<Identifier> identifiers() {
    return this.identifiers;
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
    return Objects.hash(this.identifiers, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof RemoveRolesRequest that)
        && Objects.equals(this.identifiers, that.identifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "RemoveRolesRequest{"
        + "identifiers="
        + identifiers
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final Set<Identifier> identifiers = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(RemoveRolesRequest request) {
      this.identifiers.addAll(request.identifiers);
      this.auditParentId = request.auditParentId;
    }

    public Builder identifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.identifiers.clear();
      this.identifiers.addAll(listBuilder.build());
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public RemoveRolesRequest build() {
      return new RemoveRolesRequest(this);
    }
  }
}
