package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

/**
 * The <code>UserRepositoryException</code> is the base class for all exceptions related to user
 * repository operations. It serves as the root exception for handling repository errors and
 * unexpected conditions.
 */
public abstract class UserRepositoryException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public UserRepositoryException() {}

  public UserRepositoryException(String message) {
    super(message);
  }

  public UserRepositoryException(String message, Throwable t) {
    super(message, t);
  }
}
