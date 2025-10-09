package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.base.IdentifierListBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(builder = CreatePrivilegeRequest.Builder.class)
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.LawOfDemeter"})
public final class CreatePrivilegeRequest {

  @NotNull private final Privilege privilege;

  @NotNull private final List<Identifier> roleIdentifiers;

  @Nullable private final String auditParentId;

  private CreatePrivilegeRequest(Builder builder) {
    this.privilege = builder.privilege;
    this.roleIdentifiers = List.copyOf(builder.roleIdentifiers);
    this.auditParentId = builder.auditParentId;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NotNull
  public Privilege privilege() {
    return this.privilege;
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
    return Objects.hash(this.privilege, this.roleIdentifiers, this.auditParentId);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CreatePrivilegeRequest that)
        && Objects.equals(this.privilege, that.privilege)
        && Objects.equals(this.roleIdentifiers, that.roleIdentifiers)
        && Objects.equals(this.auditParentId, that.auditParentId);
  }

  @Override
  public String toString() {
    return "CreatePrivilegeRequest{"
        + "privilege="
        + privilege
        + ", roleIdentifiers="
        + roleIdentifiers
        + ", auditParentId='"
        + auditParentId
        + '\''
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private Privilege privilege;
    private final Set<Identifier> roleIdentifiers = new TreeSet<>();
    private String auditParentId;

    private Builder() {}

    private Builder(CreatePrivilegeRequest request) {
      this.privilege = request.privilege;
      this.roleIdentifiers.addAll(request.roleIdentifiers);
      this.auditParentId = request.auditParentId;
    }

    public Builder privilege(Privilege privilege) {
      this.privilege = privilege;
      return this;
    }

    public Builder roleIdentifiers(Consumer<IdentifierListBuilder> configurer) {
      IdentifierListBuilder listBuilder = new IdentifierListBuilder();
      configurer.accept(listBuilder);
      this.roleIdentifiers.clear();
      this.roleIdentifiers.addAll(listBuilder.build());
      return this;
    }

    public Builder auditParentId(String auditParentId) {
      this.auditParentId = auditParentId;
      return this;
    }

    public CreatePrivilegeRequest build() {
      Objects.requireNonNull(this.privilege, "Privilege must not be null");
      return new CreatePrivilegeRequest(this);
    }
  }
}
