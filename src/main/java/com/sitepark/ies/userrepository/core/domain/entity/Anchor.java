package com.sitepark.ies.userrepository.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAnchorException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * An "anchor" in the IES system is an additional optional field that serves as an alternative
 * identifier. This identifier, like an ID, is unique. An anchor may only use the following
 * characters:
 *
 * <ul>
 *   <li><code>A-Z</code>
 *   <li><code>a-z</code>
 *   <li><code>0-9</code>
 *   <li><code>_</code> (underscore)
 *   <li><code>-</code> (minus)
 *   <li><code>.</code> (dot)
 * </ul>
 *
 * <p>An anchor can be assigned for each user or content entity. Anchors can be changed or
 * transferred to other entities. However, they must always be unique. For anchor there are several
 * use cases:
 *
 * <ul>
 *   <li>Foreign key mapping for data imports from external systems
 *   <li>ID mappings for data imports from other IES installations
 *   <li>As coded reference application logic or search queries
 * </ul>
 */
public final class Anchor implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  public static final String VALID_CHARS_REGEX = "[a-zA-Z0-9_.\\-]+";

  private static final Pattern VALIDATOR_PATTERN = Pattern.compile(VALID_CHARS_REGEX);

  private static final Pattern ONLY_NUMBERS_PATTERN = Pattern.compile("[0-9]+");

  /** Used to reset anchor when saving entries. */
  public static final Anchor EMPTY = new Anchor("");

  @JsonValue private final String name;

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
   * @throws InvalidAnchorException if the anchor is invalid
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
    return (o instanceof Anchor anchor) && Objects.equals(this.name, anchor.name);
  }

  @Override
  public String toString() {
    return this == EMPTY ? "EMPTY" : this.name;
  }
}
