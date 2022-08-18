package com.sitepark.ies.userrepository.core.domain.exception;

public abstract class UserRepositoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserRepositoryException() {
		super();
	}
	public UserRepositoryException(String message) {
		super(message);
	}
}
