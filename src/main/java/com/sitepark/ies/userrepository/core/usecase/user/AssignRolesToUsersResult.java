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
 *   <li>{@link Assigned} - Roles were successfully assigned to users
 *   <li>{@link Skipped} - The role assignment was skipped (no effective assignments)
 * </ul>
 *
 * <p>The {@link Assigned} variant contains assignment information that can be used for audit
 * logging or tracking what roles were assigned to which users.
 */
public sealed interface AssignRolesToUsersResult {

  /**
   * Gets the user-role assignments.
   *
   * @return the user-role assignments
   */
  @NotNull
  UserRoleAssignment assignments();

  /**
   * Result when roles were successfully assigned.
   *
   * @param assignments the effective user-role assignments that were made
   * @param timestamp the timestamp when the assignment occurred
   */
  record Assigned(@NotNull UserRoleAssignment assignments, @NotNull Instant timestamp)
      implements AssignRolesToUsersResult {}

  /**
   * Result when the role assignment was skipped.
   *
   * @param assignments empty assignments (no effective changes)
   */
  record Skipped(@NotNull UserRoleAssignment assignments) implements AssignRolesToUsersResult {
    /**
     * Creates a Skipped result with empty assignments.
     */
    public Skipped() {
      this(UserRoleAssignment.builder().build());
    }
  }

  /**
   * Factory method for assigned result.
   *
   * @param assignments the effective user-role assignments
   * @param timestamp the assignment timestamp
   * @return assigned result
   */
  static AssignRolesToUsersResult assigned(
      @NotNull UserRoleAssignment assignments, @NotNull Instant timestamp) {
    return new Assigned(assignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty assignments
   */
  static AssignRolesToUsersResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful assignment (not skipped).
   *
   * @return true if roles were assigned, false if skipped
   */
  default boolean wasAssigned() {
    return this instanceof Assigned;
  }
}
