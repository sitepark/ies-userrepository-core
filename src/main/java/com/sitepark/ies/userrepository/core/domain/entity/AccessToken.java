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

@JsonDeserialize(builder = AccessToken.Builder.class)
public final class AccessToken {

	private final long id;

	private final long user;

	private final String name;

	private final String token;

	private final OffsetDateTime createdAt;

	private final OffsetDateTime expiresAt;

	private final OffsetDateTime lastUsed;

	private final List<String> scopes;

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
		this.scopes = Collections.unmodifiableList(builder.scopes);
		this.impersonation = builder.impersonation;
		this.active = builder.active;
		this.revoked = builder.revoked;
	}

	public Optional<Long> getId() {
		if (this.id == 0) {
			return Optional.empty();
		} else {
			return Optional.of(this.id);
		}
	}

	public long getUser() {
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
	public List<String> getScopes() {
		return this.scopes;
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
				this.scopes,
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
		} else if (!Objects.equals(this.scopes, accessToken.scopes)) {
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

		private long id;

		private long user;

		private String name;

		private String token;

		private OffsetDateTime createdAt;

		private OffsetDateTime expiresAt;

		private OffsetDateTime lastUsed;

		private final List<String> scopes = new ArrayList<>();

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
			this.scopes.addAll(accessToken.scopes);
			this.impersonation = accessToken.impersonation;
			this.active = accessToken.active;
			this.revoked = accessToken.revoked;
		}

		public Builder id(long id) {
			assert id > 0 : "id must be greater than 0";
			this.id = id;
			return this;
		}

		public Builder user(long user) {
			assert user > 0 : "user must be greater than 0";
			this.user = user;
			return this;
		}

		public Builder name(String name) {
			assert name != null : "name is null";
			assert !name.isBlank() : "name is blank";
			this.name = name;
			return this;
		}

		public Builder token(String token) {
			assert token != null : "token is null";
			assert !token.isBlank() : "token is blank";
			this.token = token;
			return this;
		}

		public Builder createdAt(OffsetDateTime createdAt) {
			assert createdAt != null : "createdAt is null";
			this.createdAt = createdAt;
			return this;
		}

		public Builder expiresAt(OffsetDateTime expiresAt) {
			assert expiresAt != null : "expiresAt is null";
			this.expiresAt = expiresAt;
			return this;
		}

		public Builder lastUsed(OffsetDateTime lastUsed) {
			assert lastUsed != null : "lastUsed is null";
			this.lastUsed = lastUsed;
			return this;
		}

		@JsonSetter
		public Builder scopes(List<String> scopes) {
			assert scopes != null : "scopes is null";
			this.scopes.clear();
			for (String scope : scopes) {
				this.scope(scope);
			}
			return this;
		}

		public Builder scopes(String... scopes) {
			assert scopes != null : "scopes is null";
			this.scopes.clear();
			for (String scope : scopes) {
				this.scope(scope);
			}
			return this;
		}

		public Builder scope(String scope) {
			assert scope != null : "scope is null";
			assert !scope.isBlank() : "scope is blank";
			this.scopes.add(scope);
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

			assert user > 0 : "user not set";
			assert name != null : "name not set";

			return new AccessToken(this);
		}

	}
}
