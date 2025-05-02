package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

/**
 * The <code>AccessTokenDigestException</code> exception is thrown if the access token could not be
 * digested.
 */
public class AccessTokenDigestException extends UserRepositoryException {

  @Serial private static final long serialVersionUID = 1L;

  public AccessTokenDigestException(String message) {
    super(message);
  }

  public AccessTokenDigestException(String message, Throwable t) {
    super(message, t);
  }
}
