package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a privilege unassignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Unassigned} - Privileges were successfully unassigned from roles
 *   <li>{@link Skipped} - The privilege unassignment was skipped (no effective unassignments)
 * </ul>
 */
public sealed interface UnassignPrivilegesFromRolesResult {

  /**
   * Gets the role-privilege unassignments.
   *
   * @return the role-privilege unassignments
   */
  @NotNull
  RolePrivilegeAssignment unassignments();

  /**
   * Result when privileges were successfully unassigned.
   *
   * @param unassignments the effective role-privilege unassignments that were made
   * @param timestamp the timestamp when the unassignment occurred
   */
  record Unassigned(@NotNull RolePrivilegeAssignment unassignments, @NotNull Instant timestamp)
      implements UnassignPrivilegesFromRolesResult {}

  /**
   * Result when the privilege unassignment was skipped.
   *
   * @param unassignments empty unassignments (no effective changes)
   */
  record Skipped(@NotNull RolePrivilegeAssignment unassignments)
      implements UnassignPrivilegesFromRolesResult {
    /** Creates a Skipped result with empty unassignments. */
    public Skipped() {
      this(RolePrivilegeAssignment.builder().build());
    }
  }

  /**
   * Factory method for unassigned result.
   *
   * @param unassignments the effective role-privilege unassignments
   * @param timestamp the unassignment timestamp
   * @return unassigned result
   */
  static UnassignPrivilegesFromRolesResult unassigned(
      @NotNull RolePrivilegeAssignment unassignments, @NotNull Instant timestamp) {
    return new Unassigned(unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty unassignments
   */
  static UnassignPrivilegesFromRolesResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful unassignment (not skipped).
   *
   * @return true if privileges were unassigned, false if skipped
   */
  default boolean wasUnassigned() {
    return this instanceof Unassigned;
  }
}
