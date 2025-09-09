package com.sitepark.ies.userrepository.core.domain.value.identity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.value.Identity;
import java.util.Objects;

/**
 * The <code>LdapIdentity</code> class represents an identity provider using LDAP for user
 * authentication. It facilitates user authentication and access control using LDAP credentials.
 */
@JsonDeserialize(builder = LdapIdentity.Builder.class)
public final class LdapIdentity implements Identity {

  private final String serverId;

  private final String dn;

  private LdapIdentity(Builder builder) {
    this.serverId = builder.serverId;
    this.dn = builder.dn;
  }

  public String getServerId() {
    return this.serverId;
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
    return Objects.hash(this.serverId, this.dn);
  }

  @Override
  public String toString() {
    return "LdapIdentity [serverId=" + this.serverId + ", dn=" + this.dn + "]";
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof LdapIdentity that)) {
      return false;
    }

    return Objects.equals(this.serverId, that.serverId) && Objects.equals(this.dn, that.dn);
  }

  @JsonPOJOBuilder(withPrefix = "")
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private String serverId;

    private String dn;

    private Builder() {}

    public Builder(LdapIdentity ldapIdentity) {
      this.serverId = ldapIdentity.serverId;
      this.dn = ldapIdentity.dn;
    }

    public Builder serverId(String serverId) {
      Objects.requireNonNull(serverId, "serverId is null");
      this.serverId = serverId;
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

      if (this.serverId == null) {
        throw new IllegalStateException("serverId is not set");
      }
      if (this.dn == null) {
        throw new IllegalStateException("dn is not set");
      }
      return new LdapIdentity(this);
    }
  }
}
