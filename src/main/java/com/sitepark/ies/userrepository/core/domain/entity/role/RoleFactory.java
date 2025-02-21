package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.Role;

/**
 * The <code>RefFactory</code> class is responsible for creating {@link Ref} objects, which can
 * reference a group using either an {@link Anchor} or an ID as a reference. This factory
 * intelligently determines whether the given string represents an Anchor or an ID and creates the
 * corresponding {@link Ref} object accordingly.
 *
 * @see Ref
 */
public class RoleFactory {

  private static final long serialVersionUID = 1L;

  public Role create(String s) {

    Role role = UserLevelRoles.valueOf(s);
    if (role != null) {
      return role;
    }

    if (Identifier.isId(s)) {
      return Ref.ofId(s);
    }

    return Ref.ofAnchor(s);
  }
}
