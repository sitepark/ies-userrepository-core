package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import java.io.Serial;

/**
 * The <code>AnchorAlreadyExistsException</code> exception is thrown when attempting to create a new
 * anchor that already exists, violating the uniqueness constraint for anchors.
 */
public class AnchorAlreadyExistsException extends UserRepositoryException {

  @Serial private static final long serialVersionUID = 1L;

  private final Anchor anchor;

  private final String owner;

  public AnchorAlreadyExistsException(Anchor anchor, String owner) {
    this.anchor = anchor;
    this.owner = owner;
  }

  public Anchor getAnchor() {
    return this.anchor;
  }

  public String getOwner() {
    return this.owner;
  }

  @Override
  public String getMessage() {
    return "Anchor " + this.anchor + " already exists for user " + this.owner;
  }
}
