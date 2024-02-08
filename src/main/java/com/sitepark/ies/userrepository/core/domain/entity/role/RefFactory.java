package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>RefFactory</code> class is responsible for creating {@link Ref} objects,
 * which can reference a group using either an {@link Anchor} or an ID as a reference.
 * This factory intelligently determines whether the given string represents an Anchor
 * or an ID and creates the corresponding {@link Ref} object accordingly.
 *
 * @see Ref
 */
public class RefFactory implements RoleFactory {

  private static final long serialVersionUID = 1L;

  private static final Pattern PATTERN_ID = Pattern.compile("^REF\\(([0-9]+)\\)$");

  private static final Pattern PATTERN_ANCHOR =
      Pattern.compile("^REF\\((" + Anchor.VALID_CHARS_REGEX + ")\\)$");

  @Override
  public boolean accept(String role) {
    return PATTERN_ID.matcher(role).matches() || PATTERN_ANCHOR.matcher(role).matches();
  }

  @Override
  public Ref create(String role) {

    Matcher idMatcher = PATTERN_ID.matcher(role);
    if (idMatcher.matches()) {
      String id = idMatcher.group(1);
      return Ref.ofId(id);
    }

    Matcher anchorMatcher = PATTERN_ANCHOR.matcher(role);
    if (anchorMatcher.matches()) {
      String anchor = anchorMatcher.group(1);
      return Ref.ofAnchor(anchor);
    }

    throw new IllegalArgumentException("Invalid role: " + role);
  }
}
