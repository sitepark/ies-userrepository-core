package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

/**
 * The <code>PrivilegeNotFoundException</code> exception is thrown when a privilege cannot be found
 * or does not exist in the system, typically when attempting to access or manipulate
 * privilege-related information for a privilege that is not present.
 */
public class PrivilegeNotFoundException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  private final String id;

  public PrivilegeNotFoundException(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  @Override
  public String getMessage() {
    return "Privilege with id " + this.id + " not found";
  }
}
