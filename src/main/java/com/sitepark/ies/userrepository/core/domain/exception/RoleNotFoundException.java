package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.sharedkernel.domain.DomainException;
import java.io.Serial;

/**
 * The <code>RoleNotFoundException</code> exception is thrown when a role cannot be found or does
 * not exist in the system, typically when attempting to access or manipulate role-related
 * information for a role that is not present.
 */
public class RoleNotFoundException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  private final String id;

  public RoleNotFoundException(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  @Override
  public String getMessage() {
    return "Role with id " + this.id + " not found";
  }
}
