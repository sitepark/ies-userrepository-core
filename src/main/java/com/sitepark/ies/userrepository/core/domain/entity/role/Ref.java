package com.sitepark.ies.userrepository.core.domain.entity.role;

import java.util.Optional;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Role;

public final class Ref extends Role {

	private final long id;

	private final Anchor anchor;

	/**
	 * For object mapping with myBatis an objectfactory is used.
	 * However, this only takes effect if a default constructor exists.
	 * @See https://github.com/mybatis/mybatis-3/blob/mybatis-3.5.13/
	 *	src/main/java/org/apache/ibatis/executor/resultset/DefaultResultSetHandler.java#L682
	 */
	@SuppressWarnings("PMD.NullAssignment")
	protected Ref() {
		super(null);
		this.id = 0L;
		this.anchor = null;
	}

	// A ref has either an id or an anchor.
	@SuppressWarnings("PMD.NullAssignment")
	private Ref(long id) {
		super("REF(" + id + ")");
		assert id > 0 : "id must be greater than 0";
		this.id = id;
		this.anchor = null;
	}

	private Ref(Anchor anchor) {
		super("REF(" + anchor + ")");
		assert anchor != null : "anchor is null";
		this.id = 0;
		this.anchor = anchor;
	}

	public static Ref ofId(long id) {
		return new Ref(id);
	}

	public static Ref ofAnchor(Anchor anchor) {
		return new Ref(anchor);
	}

	public static Ref ofAnchor(String anchor) {
		assert anchor != null : "anchor is null";
		return new Ref(Anchor.ofString(anchor));
	}

	public Optional<Long> getId() {
		if (this.id == 0) {
			return Optional.empty();
		}
		return Optional.of(this.id);
	}

	public Optional<Anchor> getAnchor() {
		return Optional.ofNullable(this.anchor);
	}
}
