package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a privilege update operation.
 *
 * <p>Contains results for both privilege data changes and role assignment changes. The privilege
 * update includes patch information (forward patch and revert patch) when changes were made.
 *
 * <p>Possible outcomes:
 *
 * <ul>
 *   <li>Privilege unchanged, roles unchanged
 *   <li>Privilege unchanged, roles changed
 *   <li>Privilege changed, roles unchanged
 *   <li>Privilege changed, roles changed
 * </ul>
 *
 * @param privilegeId the ID of the privilege
 * @param privilegeName the name of the privilege
 * @param timestamp the timestamp of the operation
 * @param patch the forward patch (changes made), null if unchanged
 * @param revertPatch the reverse patch (to revert changes), null if unchanged
 * @param roleReassignmentResult the result of role reassignments, null if no roles were reassigned
 */
public record UpdatePrivilegeResult(
    @NotNull String privilegeId,
    @NotNull String privilegeName,
    @NotNull Instant timestamp,
    @Nullable PatchDocument patch,
    @Nullable PatchDocument revertPatch,
    @Nullable ReassignRolesToPrivilegesResult roleReassignmentResult) {

  /**
   * Checks if the privilege data was changed.
   *
   * @return true if privilege was updated, false if unchanged
   */
  public boolean hasPrivilegeChanges() {
    return patch != null && !patch.isEmpty();
  }

  /**
   * Checks if roles were reassigned.
   *
   * @return true if roles were reassigned, false if skipped
   */
  public boolean hasRoleChanges() {
    return roleReassignmentResult != null && roleReassignmentResult.wasReassigned();
  }

  /**
   * Checks if any changes were made (privilege or roles).
   *
   * @return true if privilege or roles changed
   */
  public boolean hasAnyChanges() {
    return hasPrivilegeChanges() || hasRoleChanges();
  }
}
