package com.sitepark.ies.userrepository.core.domain.value;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sitepark.ies.userrepository.core.domain.value.identity.LdapIdentity;

/**
 * The <code>Identity</code> interface represents a user's identity for authentication purposes.
 * Classes like {@link LdapIdentity} implement this interface to specify how users can authenticate
 * themselves using different identity providers.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(value = LdapIdentity.class, name = "ldap")})
public interface Identity {}
