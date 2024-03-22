package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AccessTokenNotActiveException</code> exception is thrown
 * when an access token is not active, making
 * it invalid for authentication.
 */
public class AccessTokenNotActiveException extends AuthenticationFailedException {

  private static final long serialVersionUID = 1L;

  public AccessTokenNotActiveException() {}
}
