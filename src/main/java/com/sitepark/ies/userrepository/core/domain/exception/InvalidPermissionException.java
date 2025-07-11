package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

public class InvalidPermissionException extends UserRepositoryException {
  @Serial private static final long serialVersionUID = 1L;

  public InvalidPermissionException(String message) {
    super(message);
  }

  public InvalidPermissionException(String message, Throwable t) {
    super(message, t);
  }
}
