package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a privilege restore operation.
 *
 * <p>This sealed interface represents the outcome of attempting to restore a privilege. The
 * operation can either successfully restore the privilege or skip restoration if the privilege
 * already exists.
 *
 * @see Restored indicates the privilege was successfully restored
 * @see Skipped indicates restoration was skipped because privilege already exists
 */
public sealed interface RestorePrivilegeResult {

  /**
   * Privilege ID of the restore operation target.
   *
   * @return the privilege ID
   */
  @NotNull
  String privilegeId();

  /**
   * Result indicating the privilege was successfully restored.
   *
   * @param privilegeId the ID of the restored privilege
   * @param snapshot snapshot of the restored privilege data including roles
   * @param timestamp when the restore occurred
   */
  record Restored(
      @NotNull String privilegeId, @NotNull PrivilegeSnapshot snapshot, @NotNull Instant timestamp)
      implements RestorePrivilegeResult {}

  /**
   * Result indicating restoration was skipped because the privilege already exists.
   *
   * @param privilegeId the ID of the privilege that already exists
   * @param reason explanation why restoration was skipped
   */
  record Skipped(@NotNull String privilegeId, @NotNull String reason)
      implements RestorePrivilegeResult {}

  /**
   * Factory method for creating a restored result.
   *
   * @param privilegeId the ID of the restored privilege
   * @param snapshot snapshot of the restored privilege data
   * @param timestamp when the restore occurred
   * @return a Restored result
   */
  static RestorePrivilegeResult restored(
      String privilegeId, PrivilegeSnapshot snapshot, Instant timestamp) {
    return new Restored(privilegeId, snapshot, timestamp);
  }

  /**
   * Factory method for creating a skipped result.
   *
   * @param privilegeId the ID of the privilege that already exists
   * @param reason explanation why restoration was skipped
   * @return a Skipped result
   */
  static RestorePrivilegeResult skipped(String privilegeId, String reason) {
    return new Skipped(privilegeId, reason);
  }
}
