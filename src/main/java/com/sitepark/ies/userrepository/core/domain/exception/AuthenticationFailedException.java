package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

/**
 * The <code>AuthenticationFailedException</code> exception is thrown when an authentication process
 * fails, indicating that the provided credentials are invalid or authentication was unsuccessful
 * for some reason.
 */
public abstract class AuthenticationFailedException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  public AuthenticationFailedException() {}

  public AuthenticationFailedException(String msg) {
    super(msg);
  }
}
