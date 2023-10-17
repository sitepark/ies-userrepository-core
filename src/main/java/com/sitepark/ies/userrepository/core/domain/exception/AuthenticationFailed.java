package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AuthenticationFailed</code> exception is thrown when an authentication
 * process fails, indicating that the provided credentials are invalid or authentication
 * was unsuccessful for some reason.
 */
public abstract class AuthenticationFailed extends UserRepositoryException {
	private static final long serialVersionUID = 1L;

	public AuthenticationFailed() {
		super();
	}

	public AuthenticationFailed(String msg) {
		super(msg);
	}

}