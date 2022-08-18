package com.sitepark.ies.userrepository.core.domain.entity;

import java.util.Optional;

public final class User {

	private final Identifier identifier;
	private final String login;
	private final String firstname;
	private final String lastname;

	protected User(Builder builder) {
		this.identifier = builder.identifier;
		this.login = builder.login;
		this.firstname = builder.firstname;
		this.lastname = builder.lastname;
	}

	public Optional<Identifier> getIdentifier() {
		return Optional.ofNullable(this.identifier);
	}

	public String getLogin() {
		return this.login;
	}

	public Optional<String> getFirstname() {
		return Optional.ofNullable(this.firstname);
	}

	public Optional<String> getLastname() {
		return Optional.ofNullable(this.lastname);
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	public static final class Builder {

		private Identifier identifier;
		private String login;
		private String firstname;
		private String lastname;

		protected Builder() {
		}

		protected Builder(User user) {
			this.identifier = user.identifier;
			this.login = user.login;
			this.firstname = user.firstname;
			this.lastname = user.lastname;
		}

		public Builder identifier(Identifier identifier) {
			assert identifier != null;
			this.identifier = identifier;
			return this;
		}

		public Builder login(String login) {
			assert login != null;
			assert !login.isBlank();
			this.login = login;
			return this;
		}

		public Builder firstname(String firstname) {
			assert firstname != null;
			assert !firstname.isBlank();
			this.firstname = firstname;
			return this;
		}

		public Builder lastname(String lastname) {
			assert lastname != null;
			assert !lastname.isBlank();
			this.lastname = lastname;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}
}
