package com.sitepark.ies.userrepository.core.domain.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An access token enables authentication as a user
 * without specifying a username and password.
 */
@JsonDeserialize(builder = AccessToken.Builder.class)
public final class AccessToken {

	private final String id;

	private final String user;

	private final String name;

	private final String token;

	private final OffsetDateTime createdAt;

	private final OffsetDateTime expiresAt;

	private final OffsetDateTime lastUsed;

	private final List<String> scopeList;

	private final boolean impersonation;

	private final boolean active;

	private final boolean revoked;

	protected AccessToken(Builder builder) {
		this.id = builder.id;
		this.user = builder.user;
		this.name = builder.name;
		this.token = builder.token;
		this.createdAt = builder.createdAt;
		this.expiresAt = builder.expiresAt;
		this.lastUsed = builder.lastUsed;
		this.scopeList = Collections.unmodifiableList(builder.scopeList);
		this.impersonation = builder.impersonation;
		this.active = builder.active;
		this.revoked = builder.revoked;
	}

	public Optional<String> getId() {
		if (this.id == null) {
			return Optional.empty();
		} else {
			return Optional.of(this.id);
		}
	}

	public String getUser() {
		return this.user;
	}

	public String getName() {
		return this.name;
	}

	public Optional<String> getToken() {
		return Optional.ofNullable(this.token);
	}

	public Optional<OffsetDateTime> getCreatedAt() {
		return Optional.ofNullable(this.createdAt);
	}

	public Optional<OffsetDateTime> getExpiresAt() {
		return Optional.ofNullable(this.expiresAt);
	}

	public Optional<OffsetDateTime> getLastUsed() {
		return Optional.ofNullable(this.lastUsed);
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public List<String> getScopeList() {
		return this.scopeList;
	}

	public boolean isImpersonation() {
		return this.impersonation;
	}

	public boolean isActive() {
		return this.active;
	}

	public boolean isRevoked() {
		return this.revoked;
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
				this.user,
				this.name,
				this.token,
				this.createdAt,
				this.expiresAt,
				this.lastUsed,
				this.scopeList,
				this.impersonation,
				this.active,
				this.revoked);
	}

	@Override
	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.NPathComplexity"
	})
	public final boolean equals(Object o) {

		if (!(o instanceof AccessToken)) {
			return false;
		}

		AccessToken accessToken = (AccessToken)o;

		if (!Objects.equals(this.id, accessToken.id)) {
			return false;
		} else if (!Objects.equals(this.user, accessToken.user)) {
			return false;
		} else if (!Objects.equals(this.name, accessToken.name)) {
			return false;
		} else if (!Objects.equals(this.token, accessToken.token)) {
			return false;
		} else if (!Objects.equals(this.createdAt, accessToken.createdAt)) {
			return false;
		} else if (!Objects.equals(this.expiresAt, accessToken.expiresAt)) {
			return false;
		} else if (!Objects.equals(this.lastUsed, accessToken.lastUsed)) {
			return false;
		} else if (!Objects.equals(this.scopeList, accessToken.scopeList)) {
			return false;
		} else if (!Objects.equals(this.impersonation, accessToken.impersonation)) {
			return false;
		} else if (!Objects.equals(this.active, accessToken.active)) {
			return false;
		} else if (!Objects.equals(this.revoked, accessToken.revoked)) {
			return false;
		}

		return true;
	}

	@SuppressWarnings("PMD.TooManyMethods")
	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public static final class Builder {

		private String id;

		private String user;

		private String name;

		private String token;

		private OffsetDateTime createdAt;

		private OffsetDateTime expiresAt;

		private OffsetDateTime lastUsed;

		private final List<String> scopeList = new ArrayList<>();

		private boolean impersonation;

		private boolean active = true;

		private boolean revoked;

		protected Builder() {
		}

		protected Builder(AccessToken accessToken) {
			this.id = accessToken.id;
			this.user = accessToken.user;
			this.name = accessToken.name;
			this.token = accessToken.token;
			this.createdAt = accessToken.createdAt;
			this.expiresAt = accessToken.expiresAt;
			this.lastUsed = accessToken.lastUsed;
			this.scopeList.addAll(accessToken.scopeList);
			this.impersonation = accessToken.impersonation;
			this.active = accessToken.active;
			this.revoked = accessToken.revoked;
		}

		public Builder id(String id) {
			Objects.requireNonNull(id, "id is null");
			if (!Identifier.isId(id)) {
				throw new IllegalArgumentException(id + " is not an id");
			}
			this.id = id;
			return this;
		}

		public Builder user(String user) {
			Objects.requireNonNull(user, "user is null");
			if (!Identifier.isId(user)) {
				throw new IllegalArgumentException(user + " is not an user id");
			}
			this.user = user;
			return this;
		}

		public Builder name(String name) {
			Objects.requireNonNull(name, "name is null");
			this.requireNonBlank(name, "name is blank");
			this.name = name;
			return this;
		}

		public Builder token(String token) {
			Objects.requireNonNull(token, "token is null");
			this.requireNonBlank(token, "token is blank");
			this.token = token;
			return this;
		}

		public Builder createdAt(OffsetDateTime createdAt) {
			Objects.requireNonNull(createdAt, "createdAt is null");
			this.createdAt = createdAt;
			return this;
		}

		public Builder expiresAt(OffsetDateTime expiresAt) {
			Objects.requireNonNull(expiresAt, "expiresAt is null");
			this.expiresAt = expiresAt;
			return this;
		}

		public Builder lastUsed(OffsetDateTime lastUsed) {
			Objects.requireNonNull(lastUsed, "lastUsed is null");
			this.lastUsed = lastUsed;
			return this;
		}

		@JsonSetter
		public Builder scopeList(List<String> scopeList) {
			Objects.requireNonNull(scopeList, "scopeList is null");
			this.scopeList.clear();
			for (String scope : scopeList) {
				this.scope(scope);
			}
			return this;
		}

		public Builder scopeList(String... scopeList) {
			Objects.requireNonNull(scopeList, "scopeList is null");
			this.scopeList.clear();
			for (String scope : scopeList) {
				this.scope(scope);
			}
			return this;
		}

		public Builder scope(String scope) {
			Objects.requireNonNull(scope, "scope is null");
			this.requireNonBlank(scope, "scope is blank");
			this.scopeList.add(scope);
			return this;
		}

		public Builder impersonation(boolean impersonation) {
			this.impersonation = impersonation;
			return this;
		}

		public Builder active(boolean active) {
			this.active = active;
			return this;
		}

		public Builder revoked(boolean revoked) {
			this.revoked = revoked;
			return this;
		}

		public AccessToken build() {

			if (this.user == null) {
				throw new IllegalStateException("user is not set");
			}
			if (this.name == null) {
				throw new IllegalStateException("name is not set");
			}

			return new AccessToken(this);
		}

		private void requireNonBlank(String s, String message) {
			if (s.isBlank()) {
				throw new IllegalArgumentException(message);
			}
		}
	}
}
