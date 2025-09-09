package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

public class RemoveUserFailedException extends UserRepositoryException {
  @Serial private static final long serialVersionUID = 1L;

  public RemoveUserFailedException(String message) {
    super(message);
  }

  public RemoveUserFailedException(String message, Throwable t) {
    super(message, t);
  }
}
