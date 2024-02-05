package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>UserNotFoundException</code> exception is thrown when a user cannot be found or does not exist
 * in the system, typically when attempting to access or manipulate user-related information for
 * a user that is not present.
 */
public class UserNotFoundException extends UserRepositoryException {

	private static final long serialVersionUID = 1L;

	private final String id;

	public UserNotFoundException(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String getMessage() {
		return "User with id " + this.id + " not found";
	}
}
