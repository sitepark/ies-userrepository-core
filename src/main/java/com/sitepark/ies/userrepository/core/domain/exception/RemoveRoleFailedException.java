package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

public class RemoveRoleFailedException extends UserRepositoryException {
  @Serial private static final long serialVersionUID = 1L;

  public RemoveRoleFailedException(String message) {
    super(message);
  }

  public RemoveRoleFailedException(String message, Throwable t) {
    super(message, t);
  }
}
