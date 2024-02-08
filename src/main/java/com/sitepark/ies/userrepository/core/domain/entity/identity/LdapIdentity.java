package com.sitepark.ies.userrepository.core.domain.entity.identity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.Identity;
import java.util.Objects;

/**
 * The <code>LdapIdentity</code> class represents an identity provider using
 * LDAP for user authentication. It facilitates user authentication and access
 * control using LDAP credentials.
 */
@JsonDeserialize(builder = LdapIdentity.Builder.class)
public final class LdapIdentity implements Identity {

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
  public int hashCode() {
    int hash = this.server;
    return (this.dn != null) ? (31 * hash) + this.dn.hashCode() : hash;
  }

  @Override
  public String toString() {
    return "LdapIdentity [server=" + this.server + ", dn=" + this.dn + "]";
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof LdapIdentity entity)) {
      return false;
    }

    if ((this.server != entity.server) || !Objects.equals(this.dn, entity.dn)) {
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
      if (server <= 0) {
        throw new IllegalArgumentException("server should be greater then 0");
      }
      this.server = server;
      return this;
    }

    public Builder dn(String dn) {
      Objects.requireNonNull(dn, "dn is null");
      if (dn.isBlank()) {
        throw new IllegalArgumentException("dn should not be blank");
      }
      this.dn = dn;
      return this;
    }

    public LdapIdentity build() {

      if (this.server == 0) {
        throw new IllegalStateException("server is not set");
      }
      if (this.dn == null) {
        throw new IllegalStateException("dn is not set");
      }
      return new LdapIdentity(this);
    }
  }
}
