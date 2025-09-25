package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class UpdateUserFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public UpdateUserFailedException(String message) {
    super(message);
  }

  public UpdateUserFailedException(String message, Throwable t) {
    super(message, t);
  }
}
