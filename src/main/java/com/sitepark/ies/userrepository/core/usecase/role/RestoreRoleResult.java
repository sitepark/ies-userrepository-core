package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/**
 * Result of a role restore operation.
 *
 * <p>This sealed interface represents the outcome of attempting to restore a role. The operation
 * can either successfully restore the role or skip restoration if the role already exists.
 *
 * @see Restored indicates the role was successfully restored
 * @see Skipped indicates restoration was skipped because role already exists
 */
public sealed interface RestoreRoleResult {

  /**
   * Role ID of the restore operation target.
   *
   * @return the role ID
   */
  @NotNull
  String roleId();

  /**
   * Result indicating the role was successfully restored.
   *
   * @param roleId the ID of the restored role
   * @param snapshot snapshot of the restored role data including users and privileges
   * @param timestamp when the restore occurred
   */
  record Restored(
      @NotNull String roleId, @NotNull RoleSnapshot snapshot, @NotNull Instant timestamp)
      implements RestoreRoleResult {}

  /**
   * Result indicating restoration was skipped because the role already exists.
   *
   * @param roleId the ID of the role that already exists
   * @param reason explanation why restoration was skipped
   */
  record Skipped(@NotNull String roleId, @NotNull String reason) implements RestoreRoleResult {}

  /**
   * Factory method for creating a restored result.
   *
   * @param roleId the ID of the restored role
   * @param snapshot snapshot of the restored role data
   * @param timestamp when the restore occurred
   * @return a Restored result
   */
  static RestoreRoleResult restored(String roleId, RoleSnapshot snapshot, Instant timestamp) {
    return new Restored(roleId, snapshot, timestamp);
  }

  /**
   * Factory method for creating a skipped result.
   *
   * @param roleId the ID of the role that already exists
   * @param reason explanation why restoration was skipped
   * @return a Skipped result
   */
  static RestoreRoleResult skipped(String roleId, String reason) {
    return new Skipped(roleId, reason);
  }
}
