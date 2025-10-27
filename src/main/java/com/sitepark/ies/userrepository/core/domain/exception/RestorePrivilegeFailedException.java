package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

public class RestorePrivilegeFailedException extends DomainException {
  @Serial private static final long serialVersionUID = 1L;

  public RestorePrivilegeFailedException(String message) {
    super(message);
  }

  public RestorePrivilegeFailedException(String message, Throwable t) {
    super(message, t);
  }
}
