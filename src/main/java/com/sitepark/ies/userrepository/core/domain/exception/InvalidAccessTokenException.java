package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

/**
 * The <code>InvalidAccessTokenException</code> exception is thrown when an access token provided
 * for authentication is invalid.
 */
public class InvalidAccessTokenException extends AuthenticationFailedException {

  @Serial private static final long serialVersionUID = 1L;

  public InvalidAccessTokenException(String msg) {
    super(msg);
  }
}
