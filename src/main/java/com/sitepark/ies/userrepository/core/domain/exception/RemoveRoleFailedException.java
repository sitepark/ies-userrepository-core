package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class RemoveRoleFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public RemoveRoleFailedException(String message) {
    super(message);
  }

  public RemoveRoleFailedException(String message, Throwable t) {
    super(message, t);
  }
}
