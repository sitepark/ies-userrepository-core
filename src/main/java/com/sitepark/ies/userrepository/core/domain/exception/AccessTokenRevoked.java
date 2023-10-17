package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AccessTokenRevoked</code> exception is thrown when an access token has been
 * explicitly revoked, rendering it unusable for authentication or authorization.
 */
public class AccessTokenRevoked extends AuthenticationFailed {

	private static final long serialVersionUID = 1L;

	public AccessTokenRevoked() {
		super();
	}
}