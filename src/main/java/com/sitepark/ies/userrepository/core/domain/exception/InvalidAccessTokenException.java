package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>InvalidAccessTokenException</code> exception is thrown when an access token provided
 * for authentication is invalid.
 */
public class InvalidAccessTokenException extends AuthenticationFailedException {

  private static final long serialVersionUID = 1L;

  public InvalidAccessTokenException(String msg) {
    super(msg);
  }
}
