package com.sitepark.ies.userrepository.core.domain.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonValue;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAnchorException;

/**
 * <p>An "anchor" in the IES system is an additional optional field that
 * serves as an alternative identifier.
 * This identifier, like an ID, is unique. An anchor may only use
 * the following characters:
 * </p>
 * <ul>
 * <li><code>A-Z</code></li>
 * <li><code>a-z</code></li>
 * <li><code>0-9</code></li>
 * <li><code>_</code> (underscore)</li>
 * <li><code>-</code> (minus)</li>
 * <li><code>.</code> (dot)</li>
 * </ul>
 *
 * <p>
 * An anchor can be assigned for each user or content entity. Anchors can be
 * changed or transferred to other entities. However, they must always be unique.
 * </p>
 *
 * For anchor there are several use cases:
 * <ul>
 * <li>Foreign key mapping for data imports from external systems</li>
 * <li>Id mappings for data imports from other IES installations</li>
 * <li>As coded reference application logic or search queries</li>
 * </ul>
 */
public final class Anchor implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String VALID_CHARS_REGEX = "[a-zA-Z0-9_.\\-]+";

	private static final Pattern VALIDATOR_PATTERN = Pattern.compile(VALID_CHARS_REGEX);

	private static final Pattern ONLY_NUMBERS_PATTERN = Pattern.compile("[0-9]+");

	/**
	 * Used to reset anchor when saving entries.
	 */
	public static final Anchor EMPTY = new Anchor("");

	@JsonValue
	private final String name;

	private Anchor(String name) {
		this.name = name;
	}

	public static Anchor ofString(String name) {

		if (name == null) {
			return null;
		}

		if (name.isBlank()) {
			return EMPTY;
		}

		Anchor.validate(name);
		return new Anchor(name);
	}

	public String getName() {
		return this.name;
	}

	/**
	 * @throws InvalidAnchorException
	 */
	private static void validate(String name) {

		if (ONLY_NUMBERS_PATTERN.matcher(name).matches()) {
			throw new InvalidAnchorException(name, "Anchor must not only consist of numbers");
		}

		if (!VALIDATOR_PATTERN.matcher(name).matches()) {
			throw new InvalidAnchorException(name, "Anchor contains Spaces");
		}
	}

	@Override
	public int hashCode() {
		return this.name != null ? this.name.hashCode() : 0;
	}


	@Override
	public boolean equals(Object o) {

		if (!(o instanceof Anchor)) {
			return false;
		}

		Anchor anchor = (Anchor)o;
		return Objects.equals(this.name, anchor.name);
	}

	@Override
	public String toString() {
		return this == EMPTY ? "EMPTY" : this.name;
	}
}
