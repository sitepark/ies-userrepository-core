package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.domain.value.UserSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a user removal operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Removed} - The user was successfully removed
 *   <li>{@link Skipped} - The user removal was skipped (e.g., built-in administrator)
 * </ul>
 *
 * <p>The {@link Removed} variant contains snapshot information that can be used for audit logging
 * or revert operations.
 */
public sealed interface RemoveUserResult {

  /**
   * Gets the user ID.
   *
   * @return the user ID
   */
  @NotNull
  String userId();

  /**
   * Result when the user was successfully removed.
   *
   * @param userId the user ID
   * @param displayName the display name of the user
   * @param snapshot the snapshot of the user state before removal
   * @param timestamp the timestamp when the removal occurred
   */
  record Removed(
      @NotNull String userId,
      @NotNull String displayName,
      @NotNull UserSnapshot snapshot,
      @NotNull Instant timestamp)
      implements RemoveUserResult {}

  /**
   * Result when the user removal was skipped.
   *
   * @param userId the user ID
   * @param reason the reason why the removal was skipped
   */
  record Skipped(@NotNull String userId, @NotNull String reason) implements RemoveUserResult {}

  /**
   * Factory method for removed result.
   *
   * @param userId the user ID
   * @param displayName the display name of the user
   * @param snapshot the user snapshot before removal
   * @param timestamp the removal timestamp
   * @return removed result
   */
  static RemoveUserResult removed(
      @NotNull String userId,
      @NotNull String displayName,
      @NotNull UserSnapshot snapshot,
      @NotNull Instant timestamp) {
    return new Removed(userId, displayName, snapshot, timestamp);
  }

  /**
   * Factory method for skipped result.
   *
   * @param userId the user ID
   * @param reason the reason for skipping
   * @return skipped result
   */
  static RemoveUserResult skipped(@NotNull String userId, @NotNull String reason) {
    return new Skipped(userId, reason);
  }

  /**
   * Checks if this result represents a successful removal (not skipped).
   *
   * @return true if the user was removed, false if skipped
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
