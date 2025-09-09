package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

public class UpdateRoleFailedException extends UserRepositoryException {
  @Serial private static final long serialVersionUID = 1L;

  public UpdateRoleFailedException(String message) {
    super(message);
  }

  public UpdateRoleFailedException(String message, Throwable t) {
    super(message, t);
  }
}
