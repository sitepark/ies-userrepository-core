package com.sitepark.ies.userrepository.core.usecase.user;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * Result of a user update operation.
 *
 * <p>Contains results for both user data changes and role assignment changes. Each aspect is
 * represented independently, allowing for:
 *
 * <ul>
 *   <li>User unchanged, roles unchanged
 *   <li>User unchanged, roles changed
 *   <li>User changed, roles unchanged
 *   <li>User changed, roles changed
 * </ul>
 */
public record UpdateUserResult(
    String userId,
    Instant timestamp,
    UserUpdateResult userResult,
    ReassignRolesToUsersResult roleReassignmentResult) {

  /**
   * Checks if the user data was changed.
   *
   * @return true if user was updated, false if unchanged
   */
  public boolean hasUserChanges() {
    return userResult instanceof UserUpdateResult.Updated;
  }

  /**
   * Checks if roles were assigned.
   *
   * @return true if roles were assigned, false if skipped
   */
  public boolean hasRoleChanges() {
    return roleReassignmentResult.wasReassigned();
  }

  /**
   * Checks if any changes were made (user or roles).
   *
   * @return true if user or roles changed
   */
  public boolean hasAnyChanges() {
    return hasUserChanges() || hasRoleChanges();
  }

  /**
   * Gets the user update details if user was changed.
   *
   * @return the Updated result or null if unchanged
   */
  public UserUpdateResult.@Nullable Updated userUpdate() {
    return userResult instanceof UserUpdateResult.Updated updated ? updated : null;
  }

  /**
   * Gets the role assignment details if roles were assigned.
   *
   * @return the Assigned result or null if skipped
   */
  @Override
  public @Nullable ReassignRolesToUsersResult roleReassignmentResult() {
    return roleReassignmentResult instanceof ReassignRolesToUsersResult.Reassigned reassigned
        ? reassigned
        : null;
  }
}
