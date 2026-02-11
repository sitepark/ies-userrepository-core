package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.userrepository.core.domain.value.PrivilegeRoleAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a role reassignment operation for privileges.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Reassigned} - Roles were successfully reassigned to privileges
 *   <li>{@link Skipped} - The role reassignment was skipped (no effective changes)
 * </ul>
 *
 * <p>The {@link Reassigned} variant contains assignment and unassignment information that can be
 * used for audit logging or tracking what roles were assigned to or removed from which privileges.
 */
public sealed interface ReassignRolesToPrivilegesResult {

  /**
   * Result when roles were successfully reassigned.
   *
   * @param assignments the effective privilege-role assignments that were made
   * @param unassignments the effective privilege-role unassignments that were made
   * @param timestamp the timestamp when the reassignment occurred
   */
  record Reassigned(
      @NotNull PrivilegeRoleAssignment assignments,
      @NotNull PrivilegeRoleAssignment unassignments,
      @NotNull Instant timestamp)
      implements ReassignRolesToPrivilegesResult {}

  /** Result when the role reassignment was skipped. */
  record Skipped() implements ReassignRolesToPrivilegesResult {}

  /**
   * Factory method for reassigned result.
   *
   * @param assignments the effective privilege-role assignments
   * @param unassignments the effective privilege-role unassignments
   * @param timestamp the reassignment timestamp
   * @return reassigned result
   */
  static ReassignRolesToPrivilegesResult reassigned(
      @NotNull PrivilegeRoleAssignment assignments,
      @NotNull PrivilegeRoleAssignment unassignments,
      @NotNull Instant timestamp) {
    return new Reassigned(assignments, unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static ReassignRolesToPrivilegesResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful reassignment (not skipped).
   *
   * @return true if roles were reassigned, false if skipped
   */
  default boolean wasReassigned() {
    return this instanceof Reassigned;
  }
}
