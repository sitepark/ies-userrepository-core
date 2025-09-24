package com.sitepark.ies.userrepository.core.usecase;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = RemovePrivilegesRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public final class RemovePrivilegesRequest {

  private final List<Identifier> identifiers;

  @Nullable private final String auditParentId;

  private RemovePrivilegesRequest(Builder builder) {
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
    if (!(o instanceof RemovePrivilegesRequest that)) {
      return false;
    }

    return Objects.equals(this.identifiers, that.identifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
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

    private Builder(RemovePrivilegesRequest request) {
      this.identifiers.addAll(request.identifiers);
      this.auditParentId = request.auditParentId;
    }

    public Builder identifiers(Identifier... identifiers) {
      if (identifiers == null) {
        return this;
      }
      this.identifiers.clear();
      for (Identifier identifier : identifiers) {
        this.identifier(identifier);
      }
      return this;
    }

    public Builder identifiers(Collection<Identifier> identifiers) {
      if (identifiers == null) {
        return this;
      }
      this.identifiers.clear();
      for (Identifier identifier : identifiers) {
        this.identifier(identifier);
      }
      return this;
    }

    public Builder identifier(Identifier identifier) {
      if (identifier == null) {
        return this;
      }
      this.identifiers.add(identifier);
      return this;
    }

    public Builder ids(String... ids) {
      if (ids == null) {
        return this;
      }
      this.identifiers.clear();
      for (String id : ids) {
        this.id(id);
      }
      return this;
    }

    public Builder ids(Collection<String> ids) {
      if (ids == null) {
        return this;
      }
      this.identifiers.clear();
      for (String identifier : ids) {
        this.id(identifier);
      }
      return this;
    }

    public Builder id(String id) {
      if (id == null) {
        return this;
      }
      this.identifiers.add(Identifier.ofId(id));
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public RemovePrivilegesRequest build() {
      return new RemovePrivilegesRequest(this);
    }
  }
}
