package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a role unassignment operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Unassigned} - Roles were successfully unassigned from users
 *   <li>{@link Skipped} - The role unassignment was skipped (no effective unassignments)
 * </ul>
 */
public sealed interface UnassignRolesFromUsersResult {

  /**
   * Gets the user-role unassignments.
   *
   * @return the user-role unassignments
   */
  @NotNull
  UserRoleAssignment unassignments();

  /**
   * Result when roles were successfully unassigned.
   *
   * @param unassignments the effective user-role unassignments that were made
   * @param timestamp the timestamp when the unassignment occurred
   */
  record Unassigned(@NotNull UserRoleAssignment unassignments, @NotNull Instant timestamp)
      implements UnassignRolesFromUsersResult {}

  /**
   * Result when the role unassignment was skipped.
   *
   * @param unassignments empty unassignments (no effective changes)
   */
  record Skipped(@NotNull UserRoleAssignment unassignments)
      implements UnassignRolesFromUsersResult {
    /** Creates a Skipped result with empty unassignments. */
    public Skipped() {
      this(UserRoleAssignment.builder().build());
    }
  }

  /**
   * Factory method for unassigned result.
   *
   * @param unassignments the effective user-role unassignments
   * @param timestamp the unassignment timestamp
   * @return unassigned result
   */
  static UnassignRolesFromUsersResult unassigned(
      @NotNull UserRoleAssignment unassignments, @NotNull Instant timestamp) {
    return new Unassigned(unassignments, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @return skipped result with empty unassignments
   */
  static UnassignRolesFromUsersResult skipped() {
    return new Skipped();
  }

  /**
   * Checks if this result represents a successful unassignment (not skipped).
   *
   * @return true if roles were unassigned, false if skipped
   */
  default boolean wasUnassigned() {
    return this instanceof Unassigned;
  }
}
