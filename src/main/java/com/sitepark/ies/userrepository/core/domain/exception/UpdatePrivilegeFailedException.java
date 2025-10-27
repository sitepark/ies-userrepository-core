package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class UpdatePrivilegeFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public UpdatePrivilegeFailedException(String message) {
    super(message);
  }

  public UpdatePrivilegeFailedException(String message, Throwable t) {
    super(message, t);
  }
}
