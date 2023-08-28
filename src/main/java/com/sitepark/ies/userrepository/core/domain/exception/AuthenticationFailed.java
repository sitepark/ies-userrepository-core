package com.sitepark.ies.userrepository.core.domain.exception;

public abstract class AuthenticationFailed extends UserRepositoryException {
	private static final long serialVersionUID = 1L;

	public AuthenticationFailed() {
		super();
	}

	public AuthenticationFailed(String msg) {
		super(msg);
	}

}