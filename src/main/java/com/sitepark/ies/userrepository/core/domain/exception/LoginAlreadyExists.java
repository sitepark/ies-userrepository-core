package com.sitepark.ies.userrepository.core.domain.exception;

public class LoginAlreadyExists extends UserRepositoryException {

	private static final long serialVersionUID = 1L;

	private final String login;

	private final long owner;

	public LoginAlreadyExists(String login, long owner) {
		super();
		this.login = login;
		this.owner = owner;
	}

	public String getLogin() {
		return this.login;
	}

	public long getOwner() {
		return this.owner;
	}

	@Override
	public String getMessage() {
		return "Login " + this.login + " already exists for user " + this.owner;
	}

}
