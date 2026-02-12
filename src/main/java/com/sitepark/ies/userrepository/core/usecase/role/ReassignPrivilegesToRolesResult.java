package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a privilege reassignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Reassigned} - Privileges were successfully reassigned to roles
 *   <li>{@link Skipped} - The privilege reassignment was skipped (no effective changes)
 * </ul>
 */
public sealed interface ReassignPrivilegesToRolesResult {

  /**
   * Result when privileges were successfully reassigned.
   *
   * @param assignments the effective role-privilege assignments that were made
   * @param unassignments the effective role-privilege unassignments that were made
   * @param timestamp the timestamp when the reassignment occurred
   */
  record Reassigned(
      @NotNull RolePrivilegeAssignment assignments,
      @NotNull RolePrivilegeAssignment unassignments,
      @NotNull Instant timestamp)
      implements ReassignPrivilegesToRolesResult {}

  /** Result when the privilege reassignment was skipped. */
  record Skipped() implements ReassignPrivilegesToRolesResult {}

  /**
   * Factory method for reassigned result.
   *
   * @param assignments the effective role-privilege assignments
   * @param unassignments the effective role-privilege unassignments
   * @param timestamp the reassignment timestamp
   * @return reassigned result
   */
  static ReassignPrivilegesToRolesResult reassigned(
      @NotNull RolePrivilegeAssignment assignments,
      @NotNull RolePrivilegeAssignment unassignments,
      @NotNull Instant timestamp) {
    return new Reassigned(assignments, unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static ReassignPrivilegesToRolesResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful reassignment (not skipped).
   *
   * @return true if privileges were reassigned, false if skipped
   */
  default boolean wasReassigned() {
    return this instanceof Reassigned;
  }
}
