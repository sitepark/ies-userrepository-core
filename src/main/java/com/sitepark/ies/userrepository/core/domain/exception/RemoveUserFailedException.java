package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class RemoveUserFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public RemoveUserFailedException(String message) {
    super(message);
  }

  public RemoveUserFailedException(String message, Throwable t) {
    super(message, t);
  }
}
