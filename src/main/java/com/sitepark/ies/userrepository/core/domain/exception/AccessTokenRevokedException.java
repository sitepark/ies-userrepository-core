package com.sitepark.ies.userrepository.core.domain.exception;

/**
 * The <code>AccessTokenRevokedException</code> exception is thrown when an access token has been
 * explicitly revoked, rendering it unusable for authentication or authorization.
 */
public class AccessTokenRevokedException extends AuthenticationFailedException {

  private static final long serialVersionUID = 1L;

  public AccessTokenRevokedException() {}
}
