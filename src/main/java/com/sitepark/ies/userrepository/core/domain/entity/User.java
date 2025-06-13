package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.domain.value.GenderType;
import com.sitepark.ies.userrepository.core.domain.value.Identity;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.domain.value.UserValidity;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

/** Represents user */
@JsonDeserialize(builder = User.Builder.class)
public final class User {

  private final String id;

  private final Anchor anchor;

  private final String login;

  private final Password password;

  private final String firstName;

  private final String lastName;

  private final String email;

  private final GenderType gender;

  private final String description;

  private final UserValidity validity;

  private final List<Identity> identities;

  private final List<String> roleIds;

  private final OffsetDateTime createdAt;

  private final OffsetDateTime changedAt;

  private User(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.login = builder.login;
    this.password = builder.password;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.gender = builder.gender;
    this.description = builder.description;
    this.validity = builder.validity;
    this.identities = List.copyOf(builder.identities);
    this.roleIds = List.copyOf(builder.roleIds);
    this.createdAt = builder.createdAt;
    this.changedAt = builder.changedAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getId() {
    return this.id;
  }

  @JsonIgnore
  @Nullable
  public Identifier getIdentifier() {
    if (this.id != null) {
      return Identifier.ofId(this.id);
    }
    if (this.anchor != null) {
      return Identifier.ofAnchor(this.anchor);
    }
    return null;
  }

  @Nullable
  public Anchor getAnchor() {
    return this.anchor;
  }

  public String getLogin() {
    return this.login;
  }

  @Nullable
  public Password getPassword() {
    return this.password;
  }

  @Nullable
  public OffsetDateTime getCreatedAt() {
    return this.createdAt;
  }

  @Nullable
  public OffsetDateTime getChangedAt() {
    return this.changedAt;
  }

  @JsonIgnore
  public String getName() {
    StringBuilder name = new StringBuilder();
    if (this.lastName != null) {
      name.append(this.lastName);
    }
    if (this.firstName != null) {
      if (!name.isEmpty()) {
        name.append(", ");
      }
      name.append(this.firstName);
    }
    return name.toString();
  }

  @Nullable
  public String getFirstName() {
    return this.firstName;
  }

  @Nullable
  public String getLastName() {
    return this.lastName;
  }

  @Nullable
  public String getEmail() {
    return this.email;
  }

  public GenderType getGender() {
    return this.gender;
  }

  @Nullable
  public String getDescription() {
    return this.description;
  }

  public UserValidity getValidity() {
    return this.validity;
  }

  public List<Identity> getIdentities() {
    return this.identities;
  }

  @Nullable
  public <T extends Identity> T getIdentity(Class<T> type) {
    for (Identity identity : this.identities) {
      if (type.isInstance(identity)) {
        return type.cast(identity);
      }
    }
    return null;
  }

  public List<String> getRoleIds() {
    return this.roleIds;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.id,
        this.anchor,
        this.login,
        this.password,
        this.firstName,
        this.lastName,
        this.email,
        this.gender,
        this.validity,
        this.identities,
        this.description,
        this.roleIds,
        this.createdAt,
        this.changedAt);
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof User entity)) {
      return false;
    }

    return Objects.equals(this.id, entity.id)
        && Objects.equals(this.anchor, entity.anchor)
        && Objects.equals(this.login, entity.login)
        && Objects.equals(this.password, entity.password)
        && Objects.equals(this.firstName, entity.firstName)
        && Objects.equals(this.lastName, entity.lastName)
        && Objects.equals(this.email, entity.email)
        && Objects.equals(this.gender, entity.gender)
        && Objects.equals(this.description, entity.description)
        && Objects.equals(this.validity, entity.validity)
        && Objects.equals(this.identities, entity.identities)
        && Objects.equals(this.roleIds, entity.roleIds)
        && Objects.equals(this.createdAt, entity.createdAt)
        && Objects.equals(this.changedAt, entity.changedAt);
  }

  @Override
  public String toString() {
    return "User [id="
        + this.id
        + ", anchor="
        + this.anchor
        + ", login="
        + this.login
        + ", password="
        + this.password
        + ", firstname="
        + this.firstName
        + ", lastname="
        + this.lastName
        + ", email="
        + this.email
        + ", gender="
        + this.gender
        + ", note="
        + this.description
        + ", validity="
        + this.validity
        + ", identities="
        + this.identities
        + ", roles="
        + this.roleIds
        + ", createdAt="
        + this.createdAt
        + ", changedAt="
        + this.changedAt
        + "]";
  }

  @SuppressWarnings("PMD.TooManyMethods")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final List<Identity> identities = new ArrayList<>();
    private final List<String> roleIds = new ArrayList<>();
    private String id;
    private Anchor anchor;
    private String login;
    private Password password;
    private String firstName;
    private String lastName;
    private String email;
    private GenderType gender = GenderType.UNKNOWN;
    private String description;
    private UserValidity validity = UserValidity.ALWAYS_VALID;
    private OffsetDateTime createdAt;

    private OffsetDateTime changedAt;

    private Builder() {}

    private Builder(User user) {
      this.id = user.id;
      this.anchor = user.anchor;
      this.login = user.login;
      this.password = user.password;
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.email = user.email;
      this.gender = user.gender;
      this.description = user.description;
      this.validity = user.validity;
      this.identities.addAll(user.identities);
      this.roleIds.addAll(user.roleIds);
      this.createdAt = user.createdAt;
      this.changedAt = user.changedAt;
    }

    public Builder id(String id) {
      Objects.requireNonNull(id, "id is null");
      if (!Identifier.isId(id)) {
        throw new IllegalArgumentException(id + " is not an id");
      }
      this.id = id;
      return this;
    }

    public Builder identifier(Identifier identifier) {
      assert identifier.getId() != null || identifier.getAnchor() != null;
      if (identifier.getAnchor() != null) {
        this.anchor = identifier.getAnchor();
        return this;
      }
      this.id = identifier.getId();
      return this;
    }

    public Builder anchor(String anchor) {
      this.anchor = Anchor.ofString(anchor);
      return this;
    }

    public Builder anchor(Anchor anchor) {
      this.anchor = anchor;
      return this;
    }

    public Builder login(String login) {
      this.login = this.trimToNull(login);
      return this;
    }

    public Builder password(Password password) {
      this.password = password;
      return this;
    }

    public Builder firstName(String firstname) {
      this.firstName = this.trimToNull(firstname);
      return this;
    }

    public Builder lastName(String lastname) {
      this.lastName = this.trimToNull(lastname);
      return this;
    }

    public Builder email(String email) {
      this.email = this.trimToNull(email);
      return this;
    }

    public Builder gender(GenderType gender) {
      Objects.requireNonNull(gender, "gender is null");
      this.gender = gender;
      return this;
    }

    public Builder description(String note) {
      this.description = this.trimToNull(note);
      return this;
    }

    public Builder validity(UserValidity.Builder validity) {
      Objects.requireNonNull(validity, "validity is null");
      this.validity = validity.build();
      return this;
    }

    @JsonSetter
    public Builder validity(UserValidity validity) {
      Objects.requireNonNull(validity, "validity is null");
      this.validity = validity;
      return this;
    }

    @JsonSetter
    public Builder identities(List<Identity> identities) {
      Objects.requireNonNull(identities, "identities is null");
      this.identities.clear();
      for (Identity identity : identities) {
        this.identity(identity);
      }
      return this;
    }

    public Builder identities(Identity... identities) {
      Objects.requireNonNull(identities, "identities is null");
      this.identities.clear();
      for (Identity identity : identities) {
        this.identity(identity);
      }
      return this;
    }

    public Builder identity(Identity identity) {
      Objects.requireNonNull(identity, "identity is null");
      this.identities.add(identity);
      return this;
    }

    @JsonSetter
    public Builder roleIds(String... roleIds) {
      Objects.requireNonNull(roleIds, "roleIds is null");
      this.roleIds.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleIds(List<String> roleIds) {
      Objects.requireNonNull(roleIds, "roleIds is null");
      this.roleIds.clear();
      for (String roleId : roleIds) {
        this.roleId(roleId);
      }
      return this;
    }

    public Builder roleId(String roleId) {
      Objects.requireNonNull(roleId, "roleId is null");
      this.roleIds.add(roleId);
      return this;
    }

    public Builder createdAt(OffsetDateTime createdAt) {
      Objects.requireNonNull(createdAt, "createdAt is null");
      this.createdAt = createdAt;
      return this;
    }

    public Builder changedAt(OffsetDateTime changedAt) {
      Objects.requireNonNull(changedAt, "changedAt is null");
      this.changedAt = changedAt;
      return this;
    }

    public User build() {
      if (this.login == null) {
        throw new IllegalStateException("login is not set");
      }
      return new User(this);
    }

    @JsonIgnore
    private String trimToNull(String str) {
      return ((str == null) || str.isBlank()) ? null : str.trim();
    }
  }
}
