package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AccessTokenNotActive</code> exception is thrown
 * when an access token is not active, making
 * it invalid for authentication.
 */
public class AccessTokenNotActive extends AuthenticationFailed {

	private static final long serialVersionUID = 1L;

	public AccessTokenNotActive() {
		super();
	}
}