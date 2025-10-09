package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.domain.value.Address;
import com.sitepark.ies.userrepository.core.domain.value.Contact;
import com.sitepark.ies.userrepository.core.domain.value.GenderType;
import com.sitepark.ies.userrepository.core.domain.value.Identity;
import com.sitepark.ies.userrepository.core.domain.value.Organisation;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.domain.value.UserValidity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;
import org.jetbrains.annotations.Nullable;

@Immutable
@SuppressWarnings({
  "PMD.AvoidFieldNameMatchingMethodName",
  "PMD.TooManyMethods",
  "PMD.DataClass",
  "PMD.TooManyFields"
})
@JsonDeserialize(builder = User.Builder.class)
public final class User {

  @Nullable private final String id;

  @Nullable private final Anchor anchor;

  @Nullable private final String title;

  @Nullable private final String firstName;

  @Nullable private final String lastName;

  @Nullable private final String email;

  private final GenderType gender;

  @Nullable private final String description;

  @Nullable private final Instant createdAt;

  @Nullable private final Instant changedAt;

  private final String login;

  @Nullable private final Password password;

  private final List<Identity> identities;

  private final UserValidity validity;

  @Nullable private final Address address;

  @Nullable private final Contact contact;

  @Nullable private final Organisation organisation;

  private User(Builder builder) {
    this.id = builder.id;
    this.anchor = builder.anchor;
    this.title = builder.title;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.gender = builder.gender;
    this.description = builder.description;
    this.createdAt = builder.createdAt;
    this.changedAt = builder.changedAt;
    this.login = builder.login;
    this.password = builder.password;
    this.identities = List.copyOf(builder.identities);
    this.validity = builder.validity;
    this.address = builder.address;
    this.contact = builder.contact;
    this.organisation = builder.organisation;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Nullable
  public Identifier toIdentifier() {
    if (this.id != null) {
      return Identifier.ofId(this.id);
    }
    if (this.anchor != null) {
      return Identifier.ofAnchor(this.anchor);
    }
    return null;
  }

  public String toDisplayName() {
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

    if (this.title != null) {
      name.append(", ").append(this.title);
    }

    return name.toString();
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

  @JsonProperty
  public String id() {
    return this.id;
  }

  @JsonProperty
  public Anchor anchor() {
    return this.anchor;
  }

  @JsonProperty
  public String title() {
    return this.title;
  }

  @JsonProperty
  public String firstName() {
    return this.firstName;
  }

  @JsonProperty
  public String lastName() {
    return this.lastName;
  }

  @JsonProperty
  public String email() {
    return this.email;
  }

  @JsonProperty
  public GenderType gender() {
    return this.gender;
  }

  @JsonProperty
  public String description() {
    return this.description;
  }

  @JsonProperty
  public Instant createdAt() {
    return this.createdAt;
  }

  @JsonProperty
  public Instant changedAt() {
    return this.changedAt;
  }

  @JsonProperty
  public String login() {
    return this.login;
  }

  @JsonProperty
  public Password password() {
    return this.password;
  }

  @JsonProperty
  public List<Identity> identities() {
    return List.copyOf(this.identities);
  }

  @JsonProperty
  public UserValidity validity() {
    return this.validity;
  }

  @JsonProperty
  public Address address() {
    return this.address;
  }

  @JsonProperty
  public Contact contact() {
    return this.contact;
  }

  @JsonProperty
  public Organisation organisation() {
    return this.organisation;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.id,
        this.anchor,
        this.title,
        this.firstName,
        this.lastName,
        this.gender,
        this.email,
        this.description,
        this.createdAt,
        this.changedAt,
        this.login,
        this.password,
        this.identities,
        this.validity,
        this.address,
        this.contact,
        this.organisation);
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof User entity)) {
      return false;
    }

    return Objects.equals(this.id, entity.id)
        && Objects.equals(this.anchor, entity.anchor)
        && Objects.equals(this.title, entity.title)
        && Objects.equals(this.firstName, entity.firstName)
        && Objects.equals(this.lastName, entity.lastName)
        && Objects.equals(this.gender, entity.gender)
        && Objects.equals(this.email, entity.email)
        && Objects.equals(this.description, entity.description)
        && Objects.equals(this.createdAt, entity.createdAt)
        && Objects.equals(this.changedAt, entity.changedAt)
        && Objects.equals(this.login, entity.login)
        && Objects.equals(this.password, entity.password)
        && Objects.equals(this.identities, entity.identities)
        && Objects.equals(this.validity, entity.validity)
        && Objects.equals(this.address, entity.address)
        && Objects.equals(this.contact, entity.contact)
        && Objects.equals(this.organisation, entity.organisation);
  }

  @Override
  public String toString() {
    return "User{"
        + "id='"
        + id
        + '\''
        + ", anchor="
        + anchor
        + ", title='"
        + title
        + '\''
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", gender="
        + gender
        + ", description='"
        + description
        + '\''
        + ", createdAt="
        + createdAt
        + ", changedAt="
        + changedAt
        + ", login='"
        + login
        + '\''
        + ", password="
        + password
        + ", identities="
        + identities
        + ", validity="
        + validity
        + ", address="
        + address
        + ", contact="
        + contact
        + ", organisation="
        + organisation
        + '}';
  }

  @SuppressWarnings("PMD.TooManyMethods")
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String id;
    private String title;
    private Anchor anchor;
    private String firstName;
    private String lastName;
    private GenderType gender = GenderType.UNKNOWN;
    private String email;
    private String description;
    private Instant createdAt;
    private Instant changedAt;
    private String login;
    private Password password;
    private final List<Identity> identities = new ArrayList<>();
    private UserValidity validity = UserValidity.ALWAYS_VALID;
    private Address address;
    private Contact contact;
    private Organisation organisation;

    private Builder() {}

    private Builder(User user) {
      this.id = user.id;
      this.anchor = user.anchor;
      this.title = user.title;
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.email = user.email;
      this.gender = user.gender;
      this.description = user.description;
      this.createdAt = user.createdAt;
      this.changedAt = user.changedAt;
      this.login = user.login;
      this.password = user.password;
      this.identities.addAll(user.identities);
      this.validity = user.validity;
      this.address = user.address;
      this.contact = user.contact;
      this.organisation = user.organisation;
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
      Objects.requireNonNull(identifier, "identifier is null");
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

    public Builder title(String lastname) {
      this.title = this.trimToNull(lastname);
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

    public Builder createdAt(Instant createdAt) {
      Objects.requireNonNull(createdAt, "createdAt is null");
      this.createdAt = createdAt;
      return this;
    }

    public Builder address(Address address) {
      this.address = address;
      return this;
    }

    public Builder contact(Contact contact) {
      this.contact = contact;
      return this;
    }

    public Builder organisation(Organisation organisation) {
      this.organisation = organisation;
      return this;
    }

    public Builder changedAt(Instant changedAt) {
      Objects.requireNonNull(changedAt, "changedAt is null");
      this.changedAt = changedAt;
      return this;
    }

    public User build() {
      if (this.login == null) {
        throw new IllegalStateException("login is not set");
      }
      if (this.gender == null) {
        this.gender = GenderType.UNKNOWN;
      }
      if (this.validity == null) {
        this.validity = UserValidity.ALWAYS_VALID;
      }
      return new User(this);
    }

    @JsonIgnore
    private String trimToNull(String str) {
      return ((str == null) || str.isBlank()) ? null : str.trim();
    }
  }
}
