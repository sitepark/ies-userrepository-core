package com.sitepark.ies.userrepository.core.domain.exception;

public class AccessDenied extends UserRepositoryException {
	private static final long serialVersionUID = 1L;

	public AccessDenied(String message) {
		super(message);
	}
}