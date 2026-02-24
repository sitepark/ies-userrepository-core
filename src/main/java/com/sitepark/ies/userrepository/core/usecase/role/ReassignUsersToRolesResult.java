package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RoleUserAssignment;
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
 */
public sealed interface ReassignUsersToRolesResult {

  /**
   * Result when roles were successfully assigned.
   *
   * @param assignments the effective user-role assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Reassigned(
      @NotNull RoleUserAssignment assignments,
      @NotNull RoleUserAssignment unassignments,
      @NotNull Instant timestamp)
      implements ReassignUsersToRolesResult {}

  /** Result when the role assignment was skipped. */
  record Skipped() implements ReassignUsersToRolesResult {}

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective user-role assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static ReassignUsersToRolesResult reassigned(
      @NotNull RoleUserAssignment assignments,
      @NotNull RoleUserAssignment unassignments,
      @NotNull Instant timestamp) {
    return new Reassigned(assignments, unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static ReassignUsersToRolesResult skipped() {
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
