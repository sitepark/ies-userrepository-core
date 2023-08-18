package com.sitepark.ies.userrepository.core.domain.entity;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = LdapIdentity.Builder.class)
public class LdapIdentity {

	private final int server;

	private final String dn;

	private LdapIdentity(Builder builder) {
		this.server = builder.server;
		this.dn = builder.dn;
	}

	public int getServer() {
		return this.server;
	}

	public String getDn() {
		return this.dn;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	@Override
	public final int hashCode() {
		int hash = this.server;
		hash = (this.dn != null) ? 31 * hash + this.dn.hashCode() : hash;
		return hash;
	}

	@Override
	public final boolean equals(Object o) {

		if (!(o instanceof LdapIdentity)) {
			return false;
		}

		LdapIdentity entity = (LdapIdentity)o;

		if (this.server != entity.server) {
			return false;
		} else if (!Objects.equals(this.dn, entity.dn)) {
			return false;
		}

		return true;
	}
	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public static final class Builder {

		private int server;

		private String dn;

		private Builder() {}

		public Builder(LdapIdentity ldapIdentity) {
			this.server = ldapIdentity.server;
			this.dn = ldapIdentity.dn;
		}

		public Builder server(int server) {
			assert server > 0 : "server lower then 1: " + server;
			this.server = server;
			return this;
		}

		public Builder dn(String dn) {
			assert dn != null : "dn is null";
			assert !dn.isBlank() : "dn is blank";
			this.dn = dn;
			return this;
		}

		public LdapIdentity build() {
			assert this.server > 0 : "server not set";
			assert this.dn != null : "dn not set";
			return new LdapIdentity(this);
		}
	}
}
