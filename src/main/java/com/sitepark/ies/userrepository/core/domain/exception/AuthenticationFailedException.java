package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AuthenticationFailedException</code> exception is thrown when an authentication
 * process fails, indicating that the provided credentials are invalid or authentication
 * was unsuccessful for some reason.
 */
public abstract class AuthenticationFailedException extends UserRepositoryException {
  private static final long serialVersionUID = 1L;

  public AuthenticationFailedException() {
    super();
  }

  public AuthenticationFailedException(String msg) {
    super(msg);
  }
}
