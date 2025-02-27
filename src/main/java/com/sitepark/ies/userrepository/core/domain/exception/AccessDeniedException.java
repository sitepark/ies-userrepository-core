package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

/**
 * The <code>AccessDeniedException</code> exception is thrown when a user or process is denied
 * access to a resource or operation due to insufficient permissions or authorization.
 */
public class AccessDeniedException extends UserRepositoryException {
  @Serial private static final long serialVersionUID = 1L;

  public AccessDeniedException(String message) {
    super(message);
  }
}
