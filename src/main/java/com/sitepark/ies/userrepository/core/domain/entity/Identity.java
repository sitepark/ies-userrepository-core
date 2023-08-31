package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sitepark.ies.userrepository.core.domain.entity.identity.LdapIdentity;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(value = LdapIdentity.class, name = "ldap")
})
public interface Identity {
}
