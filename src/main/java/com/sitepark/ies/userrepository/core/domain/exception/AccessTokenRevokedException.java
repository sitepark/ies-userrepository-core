package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

/**
 * The <code>AccessTokenRevokedException</code> exception is thrown when an access token has been
 * explicitly revoked, rendering it unusable for authentication or authorization.
 */
public class AccessTokenRevokedException extends AuthenticationFailedException {

  @Serial private static final long serialVersionUID = 1L;

  public AccessTokenRevokedException() {}
}
