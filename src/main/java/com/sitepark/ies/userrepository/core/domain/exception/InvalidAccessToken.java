package com.sitepark.ies.userrepository.core.domain.exception;

public class InvalidAccessToken extends AuthenticationFailed {

	private static final long serialVersionUID = 1L;

	public InvalidAccessToken(String msg) {
		super(msg);
	}
}