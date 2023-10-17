package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AccessDenied</code> exception is thrown when a user or
 * process is denied access to a resource or operation due to
 * insufficient permissions or authorization.
 */
public class AccessDenied extends UserRepositoryException {
	private static final long serialVersionUID = 1L;

	public AccessDenied(String message) {
		super(message);
	}
}