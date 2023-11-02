package com.sitepark.ies.userrepository.core.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents user
 */
@JsonDeserialize(builder = User.Builder.class)
public final class User {

	private final long id;

	private final Anchor anchor;

	private final String login;

	private final String firstname;

	private final String lastname;

	private final String email;

	private final GenderType gender;

	private final String note;

	private final UserValidity validity;

	private final List<Identity> identityList;

	private final List<Role> roleList;

	protected User(Builder builder) {
		this.id = builder.id;
		this.anchor = builder.anchor;
		this.login = builder.login;
		this.firstname = builder.firstname;
		this.lastname = builder.lastname;
		this.email = builder.email;
		this.gender = builder.gender;
		this.note = builder.note;
		this.validity = builder.validity;
		this.identityList = builder.identityList;
		this.roleList = Collections.unmodifiableList(builder.roleList);
	}

	public Optional<Long> getId() {
		if (this.id == 0) {
			return Optional.empty();
		} else {
			return Optional.of(this.id);
		}
	}

	public Optional<Anchor> getAnchor() {
		return Optional.ofNullable(this.anchor);
	}

	public String getLogin() {
		return this.login;
	}

	@JsonIgnore
	public String getName() {
		StringBuilder name = new StringBuilder();
		if (this.lastname != null) {
			name.append(this.lastname);
		}
		if (this.firstname != null) {
			if (name.length() > 0) {
				name.append(", ");
			}
			name.append(this.firstname);
		}
		return name.toString();
	}

	public Optional<String> getFirstname() {
		return Optional.ofNullable(this.firstname);
	}

	public Optional<String> getLastname() {
		return Optional.ofNullable(this.lastname);
	}

	public Optional<String> getEmail() {
		return Optional.ofNullable(this.email);
	}

	public GenderType getGender() {
		return this.gender;
	}

	public Optional<String> getNote() {
		return Optional.ofNullable(this.note);
	}

	public UserValidity getValidity() {
		return this.validity;
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public List<Identity> getIdentityList() {
		return this.identityList;
	}

	public <T extends Identity> Optional<T> getIdentity(Class<T> type) {
		for (Identity identity : this.identityList) {
			if (type.isInstance(identity)) {
				return Optional.of(type.cast(identity));
			}
		}
		return Optional.empty();
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public List<Role> getRoleList() {
		return this.roleList;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(
				this.id,
				this.anchor,
				this.login,
				this.firstname,
				this.lastname,
				this.email,
				this.gender,
				this.validity,
				this.identityList,
				this.note,
				this.roleList);
	}

	@Override
	public final boolean equals(Object o) {

		if (!(o instanceof User)) {
			return false;
		}

		User entity = (User)o;

		return
				Objects.equals(this.id, entity.id) &&
				Objects.equals(this.anchor, entity.anchor) &&
				Objects.equals(this.login, entity.login) &&
				Objects.equals(this.firstname, entity.firstname) &&
				Objects.equals(this.lastname, entity.lastname) &&
				Objects.equals(this.email, entity.email) &&
				Objects.equals(this.gender, entity.gender) &&
				Objects.equals(this.note, entity.note) &&
				Objects.equals(this.validity, entity.validity) &&
				Objects.equals(this.identityList, entity.identityList) &&
				Objects.equals(this.roleList, entity.roleList);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(this.getName())
				.append(" (")
				.append("login: ").append(this.login)
				.append(", ")
				.append("id: ").append(this.id)
				.append(", ")
				.append("anchor: ").append(this.anchor)
				.append(", ")
				.append("roleList: ").append(this.roleList)
				.append(')').toString();
	}

	@SuppressWarnings("PMD.TooManyMethods")
	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public static final class Builder {

		private long id;

		private Anchor anchor;

		private String login;

		private String firstname;

		private String lastname;

		private String email;

		private GenderType gender = GenderType.UNKNOWN;

		private String note;

		private UserValidity validity = UserValidity.ALWAYS_VALID;

		private final List<Identity> identityList = new ArrayList<>();

		private final List<Role> roleList = new ArrayList<>();

		protected Builder() {
		}

		protected Builder(User user) {
			this.id = user.id;
			this.anchor = user.anchor;
			this.login = user.login;
			this.firstname = user.firstname;
			this.lastname = user.lastname;
			this.email = user.email;
			this.gender = user.gender;
			this.note = user.note;
			this.validity = user.validity;
			this.identityList.addAll(user.identityList);
			this.roleList.addAll(user.roleList);
		}

		public Builder id(long id) {
			if (id <= 0) {
				throw new IllegalArgumentException("id should be greater than 0");
			}
			this.id = id;
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

		public Builder firstname(String firstname) {
			this.firstname = this.trimToNull(firstname);
			return this;
		}

		public Builder lastname(String lastname) {
			this.lastname = this.trimToNull(lastname);
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

		public Builder note(String note) {
			this.note = this.trimToNull(note);
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
		public Builder identityList(List<Identity> identityList) {
			Objects.requireNonNull(identityList, "identityList is null");
			this.identityList.clear();
			for (Identity identity : identityList) {
				this.identity(identity);
			}
			return this;
		}

		public Builder identityList(Identity... identityList) {
			Objects.requireNonNull(identityList, "identityList is null");
			this.identityList.clear();
			for (Identity identity : identityList) {
				this.identity(identity);
			}
			return this;
		}

		public Builder identity(Identity identity) {
			Objects.requireNonNull(identity, "identity is null");
			this.identityList.add(identity);
			return this;
		}

		@JsonSetter
		public Builder roleList(Role... roleList) {
			Objects.requireNonNull(roleList, "roleList is null");
			this.roleList.clear();
			for (Role role : roleList) {
				this.role(role);
			}
			return this;
		}

		public Builder roleList(List<Role> roleList) {
			Objects.requireNonNull(roleList, "roleList is null");
			this.roleList.clear();
			for (Role role : roleList) {
				this.role(role);
			}
			return this;
		}

		public Builder role(Role role) {
			Objects.requireNonNull(role, "role is null");
			this.roleList.add(role);
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
			return (str == null || str.isBlank()) ? null : str.trim();
		}
	}
}
