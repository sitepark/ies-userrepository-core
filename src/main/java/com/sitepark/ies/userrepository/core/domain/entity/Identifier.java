package com.sitepark.ies.userrepository.core.domain.entity;

import java.util.Optional;

public final class Identifier {

	private final Long id;

	private final Anchor anchor;

	private Identifier(Long id) {
		this.id = id;
		this.anchor = null;
	}

	private Identifier(Anchor anchor) {
		this.id = null;
		this.anchor = anchor;
	}

	public static Identifier ofId(long id) {
		return new Identifier(id);
	}

	public static Identifier ofAnchor(Anchor anchor) {
		if (anchor == null) {
			throw new NullPointerException("anchor is null");
		}
		return new Identifier(anchor);
	}

	public static Identifier ofString(String identifier) {
		if (isId(identifier)) {
			return new Identifier(Long.valueOf(identifier));
		}
		return new Identifier(Anchor.ofString(identifier));
	}

	public Optional<Long> getId() {
		return Optional.ofNullable(this.id);
	}

	public Optional<Anchor> getAnchor() {
		return Optional.ofNullable(this.anchor);
	}

	private static boolean isId(String str) {

		int length = str.length();
		if (length > 19) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}
