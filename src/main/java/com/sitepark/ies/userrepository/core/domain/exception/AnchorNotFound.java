package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;

/**
 * The <code>AnchorNotFound</code> exception is thrown when an anchor cannot be found
 * or does not exist in the system, typically when trying to access or manipulate an
 * anchor that is not present.
 */
public class AnchorNotFound extends UserRepositoryException {

	private static final long serialVersionUID = 1L;

	private final Anchor anchor;

	public AnchorNotFound(Anchor anchor) {
		super();
		this.anchor = anchor;
	}

	public Anchor getAnchor() {
		return this.anchor;
	}

	@Override
	public String getMessage() {
		return "User with anchor " + this.anchor + " not found";
	}
}