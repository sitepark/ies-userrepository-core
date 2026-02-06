package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a role assignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Reassigned} - Roles were successfully assigned to users
 *   <li>{@link Skipped} - The role assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Reassigned} variant contains assignment information that can be used for audit
 * logging or tracking what roles were assigned to which users.
 */
public sealed interface ReassignRolesToUsersResult {

  /**
   * Result when roles were successfully assigned.
   *
   * @param assignments the effective user-role assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Reassigned(
      @NotNull UserRoleAssignment assignments,
      @NotNull UserRoleAssignment unassignments,
      @NotNull Instant timestamp)
      implements ReassignRolesToUsersResult {}

  /** Result when the role assignment was skipped. */
  record Skipped() implements ReassignRolesToUsersResult {}

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective user-role assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static ReassignRolesToUsersResult reassigned(
      @NotNull UserRoleAssignment assignments,
      @NotNull UserRoleAssignment unassignments,
      @NotNull Instant timestamp) {
    return new Reassigned(assignments, unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static ReassignRolesToUsersResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if roles were assigned, false if skipped
   */
  default boolean wasReassigned() {
    return this instanceof Reassigned;
  }
}
