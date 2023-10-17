package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>InvalidAccessToken</code> exception is thrown when an access token provided
 * for authentication is invalid.
 */
public class InvalidAccessToken extends AuthenticationFailed {

	private static final long serialVersionUID = 1L;

	public InvalidAccessToken(String msg) {
		super(msg);
	}
}