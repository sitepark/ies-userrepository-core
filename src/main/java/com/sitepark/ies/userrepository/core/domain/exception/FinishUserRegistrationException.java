package com.sitepark.ies.userrepository.core.domain.exception;

import java.io.Serial;

public class FinishUserRegistrationException extends UserRepositoryException {
  @Serial private static final long serialVersionUID = 1L;

  public FinishUserRegistrationException(String message, Throwable t) {
    super(message, t);
  }
}
