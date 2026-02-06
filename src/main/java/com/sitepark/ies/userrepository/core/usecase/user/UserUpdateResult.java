package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a user data update (without role assignments).
 *
 * <p>This sealed interface represents two possible outcomes:
 *
 * <ul>
 *   <li>{@link Unchanged} - The user data was identical to stored data
 *   <li>{@link Updated} - The user data was successfully updated
 * </ul>
 */
public sealed interface UserUpdateResult {

  /** Result when no changes were detected in user data. */
  record Unchanged() implements UserUpdateResult {}

  /**
   * Result when the user data was successfully updated.
   *
   * @param displayName the display name of the user
   * @param patch the forward patch (old state to new state)
   * @param revertPatch the revert patch (new state to old state)
   */
  record Updated(
      @NotNull String displayName, @NotNull PatchDocument patch, @NotNull PatchDocument revertPatch)
      implements UserUpdateResult {}

  /**
   * Factory method for unchanged result.
   *
   * @return unchanged result
   */
  static UserUpdateResult unchanged() {
    return new Unchanged();
  }

  /**
   * Factory method for updated result.
   *
   * @param displayName the display name
   * @param patch the forward patch
   * @param revertPatch the revert patch
   * @return updated result
   */
  static UserUpdateResult updated(
      @NotNull String displayName,
      @NotNull PatchDocument patch,
      @NotNull PatchDocument revertPatch) {
    return new Updated(displayName, patch, revertPatch);
  }
}
