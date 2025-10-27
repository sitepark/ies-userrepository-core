package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a user update operation.
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Unchanged} - The user data was identical to stored data, no update performed
 *   <li>{@link Updated} - The user was successfully updated with changes
 * </ul>
 *
 * <p>The {@link Updated} variant contains patch information that can be used for audit logging or
 * other purposes.
 */
public sealed interface UpdateUserResult {

  /**
   * Gets the user ID.
   *
   * @return the user ID
   */
  @NotNull
  String userId();

  /**
   * Result when no changes were detected and no update was performed.
   *
   * @param userId the user ID
   */
  record Unchanged(@NotNull String userId) implements UpdateUserResult {}

  /**
   * Result when the user was successfully updated.
   *
   * @param userId the user ID
   * @param displayName the display name of the user
   * @param patch the forward patch (old state to new state)
   * @param revertPatch the revert patch (new state to old state)
   * @param timestamp the timestamp when the update occurred
   */
  record Updated(
      @NotNull String userId,
      @NotNull String displayName,
      @NotNull PatchDocument patch,
      @NotNull PatchDocument revertPatch,
      @NotNull Instant timestamp)
      implements UpdateUserResult {}

  /**
   * Factory method for unchanged result.
   *
   * @param userId the user ID
   * @return unchanged result
   */
  static UpdateUserResult unchanged(@NotNull String userId) {
    return new Unchanged(userId);
  }

  /**
   * Factory method for updated result.
   *
   * @param userId the user ID
   * @param displayName the display name of the user
   * @param patch the forward patch
   * @param revertPatch the revert patch
   * @param timestamp the update timestamp
   * @return updated result
   */
  static UpdateUserResult updated(
      @NotNull String userId,
      @NotNull String displayName,
      @NotNull PatchDocument patch,
      @NotNull PatchDocument revertPatch,
      @NotNull Instant timestamp) {
    return new Updated(userId, displayName, patch, revertPatch, timestamp);
  }

  /**
   * Checks if this result represents an update (not unchanged).
   *
   * @return true if the user was updated, false if unchanged
   */
  default boolean wasUpdated() {
    return this instanceof Updated;
  }

  /**
   * Gets the Updated result if this is an update, or null if unchanged.
   *
   * @return the Updated record or null
   */
  @Nullable
  default Updated asUpdated() {
    return this instanceof Updated updated ? updated : null;
  }
}
