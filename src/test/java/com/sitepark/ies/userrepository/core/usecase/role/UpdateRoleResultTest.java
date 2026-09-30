package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import java.time.Instant;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdateRoleResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdateRoleResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdateRoleResult.class).verify();
  }

  @Test
  void testHasRoleChangesWithoutPatch() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1", "name", Instant.EPOCH, null, null, ReassignPrivilegesToRolesResult.skipped());

    assertFalse(result.hasRoleChanges(), "no patch means no changes");
  }

  @Test
  void testHasRoleChangesWithEmptyPatch() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(true),
            null,
            ReassignPrivilegesToRolesResult.skipped());

    assertFalse(result.hasRoleChanges(), "empty patch means no changes");
  }

  @Test
  void testHasRoleChangesWithPatch() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(false),
            null,
            ReassignPrivilegesToRolesResult.skipped());

    assertTrue(result.hasRoleChanges(), "non-empty patch means changes");
  }

  @Test
  void testHasPrivilegeChangesWhenSkipped() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1", "name", Instant.EPOCH, null, null, ReassignPrivilegesToRolesResult.skipped());

    assertFalse(result.hasPrivilegeChanges(), "skipped reassignment means no changes");
  }

  @Test
  void testHasPrivilegeChangesWhenNoReassignmentResult() {
    UpdateRoleResult result = new UpdateRoleResult("1", "name", Instant.EPOCH, null, null, null);

    assertFalse(result.hasPrivilegeChanges(), "missing reassignment result means no changes");
  }

  @Test
  void testHasPrivilegeChangesWhenReassigned() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1",
            "name",
            Instant.EPOCH,
            null,
            null,
            ReassignPrivilegesToRolesResult.reassigned(
                RolePrivilegeAssignment.builder().build(),
                RolePrivilegeAssignment.builder().build(),
                Instant.EPOCH));

    assertTrue(result.hasPrivilegeChanges(), "reassignment means changes");
  }

  @Test
  void testHasAnyChangesWithoutChanges() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(true),
            null,
            ReassignPrivilegesToRolesResult.skipped());

    assertFalse(result.hasAnyChanges(), "nothing changed");
  }

  @Test
  void testHasAnyChangesWithPatchOnly() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(false),
            null,
            ReassignPrivilegesToRolesResult.skipped());

    assertTrue(result.hasAnyChanges(), "patch is a change");
  }

  @Test
  void testHasAnyChangesWithReassignmentOnly() {
    UpdateRoleResult result =
        new UpdateRoleResult(
            "1",
            "name",
            Instant.EPOCH,
            null,
            null,
            ReassignPrivilegesToRolesResult.reassigned(
                RolePrivilegeAssignment.builder().build(),
                RolePrivilegeAssignment.builder().build(),
                Instant.EPOCH));

    assertTrue(result.hasAnyChanges(), "reassignment is a change");
  }

  private static PatchDocument patch(boolean empty) {
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(empty);
    return patch;
  }
}
