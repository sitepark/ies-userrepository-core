package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a privilege assignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Assigned} - Privileges were successfully assigned to roles
 *   <li>{@link Skipped} - The privilege assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Assigned} variant contains assignment information that can be used for audit
 * logging or tracking what privileges were assigned to which roles.
 */
public sealed interface AssignPrivilegesToRolesResult {

  /**
   * Gets the role-privilege assignments.
   *
   * @return the role-privilege assignments
   */
  @NotNull
  RolePrivilegeAssignment assignments();

  /**
   * Result when privileges were successfully assigned.
   *
   * @param assignments the effective role-privilege assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Assigned(@NotNull RolePrivilegeAssignment assignments, @NotNull Instant timestamp)
      implements AssignPrivilegesToRolesResult {}

  /**
   * Result when the privilege assignment was skipped.
   *
   * @param assignments empty assignments (no effective changes)
   */
  record Skipped(@NotNull RolePrivilegeAssignment assignments)
      implements AssignPrivilegesToRolesResult {
    /**
     * Creates a Skipped result with empty assignments.
     */
    public Skipped() {
      this(RolePrivilegeAssignment.builder().build());
    }
  }

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective role-privilege assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static AssignPrivilegesToRolesResult assigned(
      @NotNull RolePrivilegeAssignment assignments, @NotNull Instant timestamp) {
    return new Assigned(assignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static AssignPrivilegesToRolesResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if privileges were assigned, false if skipped
   */
  default boolean wasAssigned() {
    return this instanceof Assigned;
  }
}
