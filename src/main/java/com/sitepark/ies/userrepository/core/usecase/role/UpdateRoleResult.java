package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a role update operation.
 *
 * <p>Contains results for both role data changes and privilege assignment changes. The role update
 * includes patch information (forward patch and revert patch) when changes were made.
 *
 * <p>Possible outcomes:
 *
 * <ul>
 *   <li>Role unchanged, privileges unchanged
 *   <li>Role unchanged, privileges changed
 *   <li>Role changed, privileges unchanged
 *   <li>Role changed, privileges changed
 * </ul>
 *
 * @param roleId the ID of the role
 * @param roleName the name of the role
 * @param timestamp the timestamp of the operation
 * @param patch the forward patch (changes made), null if unchanged
 * @param revertPatch the reverse patch (to revert changes), null if unchanged
 * @param privilegeReassignmentResult the result of privilege reassignments, null if no privileges
 *     were reassigned
 */
public record UpdateRoleResult(
    @NotNull String roleId,
    @NotNull String roleName,
    @NotNull Instant timestamp,
    @Nullable PatchDocument patch,
    @Nullable PatchDocument revertPatch,
    @Nullable ReassignPrivilegesToRolesResult privilegeReassignmentResult) {

  /**
   * Checks if the role data was changed.
   *
   * @return true if role was updated, false if unchanged
   */
  public boolean hasRoleChanges() {
    return patch != null && !patch.isEmpty();
  }

  /**
   * Checks if privileges were reassigned.
   *
   * @return true if privileges were reassigned, false if skipped
   */
  public boolean hasPrivilegeChanges() {
    return privilegeReassignmentResult != null && privilegeReassignmentResult.wasReassigned();
  }

  /**
   * Checks if any changes were made (role or privileges).
   *
   * @return true if role or privileges changed
   */
  public boolean hasAnyChanges() {
    return hasRoleChanges() || hasPrivilegeChanges();
  }
}
