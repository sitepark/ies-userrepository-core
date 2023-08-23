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

	private final LdapIdentity ldapIdentity;

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
		this.ldapIdentity = builder.ldapIdentity;
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

	public Optional<LdapIdentity> getLdapIdentity() {
		return Optional.ofNullable(this.ldapIdentity);
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
				this.ldapIdentity,
				this.note,
				this.roleList);
	}

	@Override
	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.NPathComplexity"
	})
	public final boolean equals(Object o) {

		if (!(o instanceof User)) {
			return false;
		}

		User entity = (User)o;

		if (!Objects.equals(this.id, entity.id)) {
			return false;
		} else if (!Objects.equals(this.anchor, entity.anchor)) {
			return false;
		} else if (!Objects.equals(this.login, entity.login)) {
			return false;
		} else if (!Objects.equals(this.firstname, entity.firstname)) {
			return false;
		} else if (!Objects.equals(this.lastname, entity.lastname)) {
			return false;
		} else if (!Objects.equals(this.email, entity.email)) {
			return false;
		} else if (!Objects.equals(this.gender, entity.gender)) {
			return false;
		} else if (!Objects.equals(this.note, entity.note)) {
			return false;
		} else if (!Objects.equals(this.validity, entity.validity)) {
			return false;
		} else if (!Objects.equals(this.ldapIdentity, entity.ldapIdentity)) {
			return false;
		} else if (!Objects.equals(this.roleList, entity.roleList)) {
			return false;
		}

		return true;
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

		private LdapIdentity ldapIdentity;

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
			this.ldapIdentity = user.ldapIdentity;
			this.roleList.addAll(user.roleList);
		}

		public Builder id(long id) {
			assert id > 0 : "id must be greater than 0";
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
			assert gender != null : "gender is null";
			this.gender = gender;
			return this;
		}

		public Builder note(String note) {
			this.note = this.trimToNull(note);
			return this;
		}

		public Builder validity(UserValidity.Builder validity) {
			assert validity != null : "validity is null";
			this.validity = validity.build();
			return this;
		}

		@JsonSetter
		public Builder validity(UserValidity validity) {
			assert validity != null : "validity is null";
			this.validity = validity;
			return this;
		}

		public Builder ldapIdentity(LdapIdentity.Builder ldapIdentity) {
			assert ldapIdentity != null : "ldapIdentity is null";
			this.ldapIdentity = ldapIdentity.build();
			return this;
		}

		@JsonSetter
		public Builder ldapIdentity(LdapIdentity ldapIdentity) {
			assert ldapIdentity != null : "ldapIdentity is null";
			this.ldapIdentity = ldapIdentity;
			return this;
		}

		@JsonSetter
		public Builder roleList(Role... roleList) {
			assert roleList != null : "roleList is null";
			for (Role role : roleList) {
				this.addRole(role);
			}
			return this;
		}

		public Builder roleList(List<Role> roleList) {
			assert roleList != null : "roleList is null";
			for (Role role : roleList) {
				this.addRole(role);
			}
			return this;
		}

		private void addRole(Role role) {
			assert role != null : "role is null";
			this.roleList.add(role);
		}

		public User build() {
			assert this.login != null : "login not set";
			return new User(this);
		}

		@JsonIgnore
		private String trimToNull(String str) {
			return (str == null || str.isBlank()) ? null : str.trim();
		}
	}
}
