package com.sitepark.ies.userrepository.core.domain.entity;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = UserValidity.Builder.class)
public class UserValidity {

	private final boolean blocked;

	private final OffsetDateTime validFrom;

	private final OffsetDateTime validTo;

	public static final UserValidity ALWAYS_VALID = new UserValidity();

	public UserValidity() {
		this(UserValidity.builder().blocked(false));
	}

	protected UserValidity(Builder builder) {
		this.blocked = builder.blocked;
		this.validFrom = builder.validFrom;
		this.validTo = builder.validTo;
	}

	public boolean isBlocked() {
		return this.blocked;
	}

	public Optional<OffsetDateTime> getValidFrom() {
		return Optional.ofNullable(this.validFrom);
	}

	public Optional<OffsetDateTime> getValidTo() {
		return Optional.ofNullable(this.validTo);
	}

	@JsonIgnore
	@SuppressWarnings("PMD.SimplifyBooleanReturns")
	public boolean isValid(OffsetDateTime base) {

		assert base != null : "base is null";

		if (this.blocked) {
			return false;
		}

		if (this.validFrom != null && this.validFrom.isAfter(base)) {
			return false;
		}

		if (this.validTo != null && this.validTo.isBefore(base)) {
			return false;
		}

		return true;
	}

	@JsonIgnore
	public boolean isNowValid() {
		return this.isValid(OffsetDateTime.now());
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	@Override
	public final int hashCode() {

		int hash = Boolean.hashCode(this.blocked);
		hash = (this.validFrom != null) ? 31 * hash + this.validFrom.hashCode() : hash;
		hash = (this.validTo != null) ? 31 * hash + this.validTo.hashCode() : hash;

		return hash;
	}

	@Override
	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.NPathComplexity"
	})
	public final boolean equals(Object o) {

		if (!(o instanceof UserValidity)) {
			return false;
		}

		UserValidity validity = (UserValidity)o;

		if (!Objects.equals(this.blocked, validity.blocked)) {
			return false;
		} else if (!Objects.equals(this.validFrom, validity.validFrom)) {
			return false;
		} else if (!Objects.equals(this.validTo, validity.validTo)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("blocked: ").append(this.blocked)
				.append(", ")
				.append("validFrom: ").append(this.validFrom)
				.append(", ")
				.append("validTo: ").append(this.validTo)
				.toString();
	}

	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public static final class Builder {

		private boolean blocked;

		private OffsetDateTime validFrom;

		private OffsetDateTime validTo;

		protected Builder() {
		}

		protected Builder(UserValidity userValidity) {
			this.blocked = userValidity.blocked;
			this.validFrom = userValidity.validFrom;
			this.validTo = userValidity.validTo;
		}

		public Builder blocked(boolean blocked) {
			this.blocked = blocked;
			return this;
		}

		public Builder validFrom(OffsetDateTime validFrom) {
			assert validFrom != null : "validFrom is null";
			this.validFrom = validFrom;
			return this;
		}

		public Builder validTo(OffsetDateTime validTo) {
			assert validTo != null : "validTo is null";
			this.validTo = validTo;
			return this;
		}

		public UserValidity build() {
			return new UserValidity(this);
		}
	}
}
