package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a user restore operation.
 *
 * <p>This sealed interface represents the outcome of attempting to restore a user. The operation
 * can either successfully restore the user or skip restoration if the user already exists.
 *
 * @see Restored indicates the user was successfully restored
 * @see Skipped indicates restoration was skipped because user already exists
 */
public sealed interface RestoreUserResult {

  /**
   * User ID of the restore operation target.
   *
   * @return the user ID
   */
  @NotNull
  String userId();

  /**
   * Result indicating the user was successfully restored.
   *
   * @param userId the ID of the restored user
   * @param snapshot snapshot of the restored user data including roles
   * @param timestamp when the restore occurred
   */
  record Restored(
      @NotNull String userId, @NotNull UserSnapshot snapshot, @NotNull Instant timestamp)
      implements RestoreUserResult {}

  /**
   * Result indicating restoration was skipped because the user already exists.
   *
   * @param userId the ID of the user that already exists
   * @param reason explanation why restoration was skipped
   */
  record Skipped(@NotNull String userId, @NotNull String reason) implements RestoreUserResult {}

  /**
   * Factory method for creating a restored result.
   *
   * @param userId the ID of the restored user
   * @param snapshot snapshot of the restored user data
   * @param timestamp when the restore occurred
   * @return a Restored result
   */
  static RestoreUserResult restored(String userId, UserSnapshot snapshot, Instant timestamp) {
    return new Restored(userId, snapshot, timestamp);
  }

  /**
   * Factory method for creating a skipped result.
   *
   * @param userId the ID of the user that already exists
   * @param reason explanation why restoration was skipped
   * @return a Skipped result
   */
  static RestoreUserResult skipped(String userId, String reason) {
    return new Skipped(userId, reason);
  }
}
