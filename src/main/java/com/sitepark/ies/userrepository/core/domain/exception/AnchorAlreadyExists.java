package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;

/**
 * The <code>AnchorAlreadyExists</code> exception is thrown when attempting to create
 * a new anchor that already exists, violating the uniqueness constraint for anchors.
 */
public class AnchorAlreadyExists extends UserRepositoryException {

	private static final long serialVersionUID = 1L;

	private final Anchor anchor;

	private final long owner;

	public AnchorAlreadyExists(Anchor anchor, long owner) {
		super();
		this.anchor = anchor;
		this.owner = owner;
	}

	public Anchor getAnchor() {
		return this.anchor;
	}

	public long getOwner() {
		return this.owner;
	}

	@Override
	public String getMessage() {
		return "Anchor " + this.anchor + " already exists for user " + this.owner;
	}

}
