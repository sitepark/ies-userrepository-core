package com.sitepark.ies.userrepository.core.domain.exception;

public class UserNotFound extends UserRepositoryException {

	private static final long serialVersionUID = 1L;

	private final long id;

	public UserNotFound(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	@Override
	public String getMessage() {
		return "User with id " + this.id + " not found";
	}
}
