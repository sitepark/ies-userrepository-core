package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>InvalidAnchorException</code> exception is thrown when an anchor provided as a reference
 * is invalid, not recognized, or does not conform to the expected format or criteria.
 */
public class InvalidAnchorException extends UserRepositoryException {

	private final String name;

	private static final long serialVersionUID = 1L;

	public InvalidAnchorException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String getMessage() {
		return "Invalid anchor '" + this.name +
				(super.getMessage() != null ? ": " + super.getMessage() : "");
	}
}
