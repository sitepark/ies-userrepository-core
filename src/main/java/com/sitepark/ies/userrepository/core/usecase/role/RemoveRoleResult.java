package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a role removal operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Removed} - The role was successfully removed
 *   <li>{@link Skipped} - The role removal was skipped (e.g., built-in administrator role)
 * </ul>
 *
 * <p>The {@link Removed} variant contains snapshot information that can be used for audit logging
 * or revert operations.
 */
public sealed interface RemoveRoleResult {

  /**
   * Gets the role ID.
   *
   * @return the role ID
   */
  @NotNull
  String roleId();

  /**
   * Result when the role was successfully removed.
   *
   * @param roleId the role ID
   * @param roleName the name of the role
   * @param snapshot the snapshot of the role state before removal
   * @param timestamp the timestamp when the removal occurred
   */
  record Removed(
      @NotNull String roleId,
      @NotNull String roleName,
      @NotNull RoleSnapshot snapshot,
      @NotNull Instant timestamp)
      implements RemoveRoleResult {}

  /**
   * Result when the role removal was skipped.
   *
   * @param roleId the role ID
   * @param reason the reason why the removal was skipped
   */
  record Skipped(@NotNull String roleId, @NotNull String reason) implements RemoveRoleResult {}

  /**
   * Factory method for removed result.
   *
   * @param roleId the role ID
   * @param roleName the name of the role
   * @param snapshot the role snapshot before removal
   * @param timestamp the removal timestamp
   * @return removed result
   */
  static RemoveRoleResult removed(
      @NotNull String roleId,
      @NotNull String roleName,
      @NotNull RoleSnapshot snapshot,
      @NotNull Instant timestamp) {
    return new Removed(roleId, roleName, snapshot, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @param roleId the role ID
   * @param reason the reason for skipping
   * @return skipped result
   */
  static RemoveRoleResult skipped(@NotNull String roleId, @NotNull String reason) {
    return new Skipped(roleId, reason);
  }

  /**
   * Checks if this result represents a successful removal (not skipped).
   *
   * @return true if the role was removed, false if skipped
   */
  default boolean wasRemoved() {
    return this instanceof Removed;
  }

  /**
   * Gets the Removed result if this is a removal, or null if skipped.
   *
   * @return the Removed record or null
   */
  @Nullable
  default Removed asRemoved() {
    return this instanceof Removed removed ? removed : null;
  }
}
