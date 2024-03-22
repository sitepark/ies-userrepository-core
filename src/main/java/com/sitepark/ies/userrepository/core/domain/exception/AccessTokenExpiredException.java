package com.sitepark.ies.userrepository.core.domain.exception;

import java.time.OffsetDateTime;

/**
 * The <code>AccessTokenExpiredException</code> exception is thrown when
 * an access token has expired and is no longer valid for
 * authentication purposes.
 */
public class AccessTokenExpiredException extends AuthenticationFailedException {

  private static final long serialVersionUID = 1L;

  private final OffsetDateTime expiredAt;

  public AccessTokenExpiredException(OffsetDateTime expiredAt) {
    this.expiredAt = expiredAt;
  }

  public OffsetDateTime getExpiredAt() {
    return this.expiredAt;
  }

  @Override
  public String getMessage() {
    return "Token has expired since " + this.expiredAt;
  }
}
