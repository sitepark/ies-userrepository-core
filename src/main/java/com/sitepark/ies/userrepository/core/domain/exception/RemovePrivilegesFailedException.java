package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class RemovePrivilegesFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public RemovePrivilegesFailedException(String message) {
    super(message);
  }

  public RemovePrivilegesFailedException(String message, Throwable t) {
    super(message, t);
  }
}
