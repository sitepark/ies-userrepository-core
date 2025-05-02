package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

/**
 * The <code>LoginAlreadyExistsException</code> exception is thrown when attempting to create a user
 * with a login or username that already exists in the system, violating the uniqueness constraint
 * for user logins.
 */
public class LoginAlreadyExistsException extends UserRepositoryException {

  @Serial private static final long serialVersionUID = 1L;

  private final String login;

  private final String owner;

  public LoginAlreadyExistsException(String login, String owner) {
    this.login = login;
    this.owner = owner;
  }

  public String getLogin() {
    return this.login;
  }

  public String getOwner() {
    return this.owner;
  }

  @Override
  public String getMessage() {
    return "Login " + this.login + " already exists for user " + this.owner;
  }
}
