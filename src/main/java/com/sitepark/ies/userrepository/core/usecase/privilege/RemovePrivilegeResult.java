package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a privilege removal operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Removed} - The privilege was successfully removed
 *   <li>{@link Skipped} - The privilege removal was skipped (e.g., built-in full access privilege)
 * </ul>
 */
public sealed interface RemovePrivilegeResult {

  /**
   * Gets the privilege ID.
   *
   * @return the privilege ID
   */
  @NotNull
  String privilegeId();

  /**
   * Result when the privilege was successfully removed.
   *
   * @param privilegeId the privilege ID
   * @param privilegeName the name of the privilege
   * @param snapshot the snapshot of the privilege state before removal
   * @param timestamp the timestamp when the removal occurred
   */
  record Removed(
      @NotNull String privilegeId,
      @NotNull String privilegeName,
      @NotNull PrivilegeSnapshot snapshot,
      @NotNull Instant timestamp)
      implements RemovePrivilegeResult {}

  /**
   * Result when the privilege removal was skipped.
   *
   * @param privilegeId the privilege ID
   * @param reason the reason why the removal was skipped
   */
  record Skipped(@NotNull String privilegeId, @NotNull String reason)
      implements RemovePrivilegeResult {}

  /**
   * Factory method for removed result.
   *
   * @param privilegeId the privilege ID
   * @param privilegeName the name of the privilege
   * @param snapshot the privilege snapshot before removal
   * @param timestamp the removal timestamp
   * @return removed result
   */
  static RemovePrivilegeResult removed(
      @NotNull String privilegeId,
      @NotNull String privilegeName,
      @NotNull PrivilegeSnapshot snapshot,
      @NotNull Instant timestamp) {
    return new Removed(privilegeId, privilegeName, snapshot, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @param privilegeId the privilege ID
   * @param reason the reason for skipping
   * @return skipped result
   */
  static RemovePrivilegeResult skipped(@NotNull String privilegeId, @NotNull String reason) {
    return new Skipped(privilegeId, reason);
  }

  /**
   * Checks if this result represents a successful removal (not skipped).
   *
   * @return true if the privilege was removed, false if skipped
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
