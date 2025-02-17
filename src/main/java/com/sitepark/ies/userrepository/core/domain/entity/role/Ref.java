package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a role that references a group, allowing for the assignment of various permissions
 * within the context of the group.
 */
@SuppressWarnings("PMD.CommentSize")
public final class Ref extends Role {

  private final String id;

  private final Anchor anchor;

  /**
   * For object mapping with myBatis an objectfactory is used. However, this only takes effect if a
   * default constructor exists.
   *
   * @see <a
   *     href="https://github.com/mybatis/mybatis-3/blob/mybatis-3.5.13/src/main/java/org/apache/ibatis/executor/resultset/DefaultResultSetHandler.java#L682">
   *     DefaultResultSetHandler.java </a>
   */
  @SuppressWarnings("PMD.NullAssignment")
  protected Ref() {
    super("NONE");
    this.id = null;
    this.anchor = null;
  }

  // A ref has either an id or an anchor.
  @SuppressWarnings("PMD.NullAssignment")
  private Ref(String id) {
    super("REF(" + id + ")");
    if (!Identifier.isId(id)) {
      throw new IllegalArgumentException(id + " not an id");
    }
    this.id = id;
    this.anchor = null;
  }

  private Ref(Anchor anchor) {
    super("REF(" + anchor + ")");
    Objects.requireNonNull(anchor, "anchor is null");
    this.id = null;
    this.anchor = anchor;
  }

  public static Ref ofId(String id) {
    return new Ref(id);
  }

  public static Ref ofAnchor(Anchor anchor) {
    return new Ref(anchor);
  }

  public static Ref ofAnchor(String anchor) {
    Objects.requireNonNull(anchor, "anchor is null");
    return new Ref(Anchor.ofString(anchor));
  }

  public Optional<String> getId() {
    if (this.id == null) {
      return Optional.empty();
    }
    return Optional.of(this.id);
  }

  public Optional<Anchor> getAnchor() {
    return Optional.ofNullable(this.anchor);
  }
}
