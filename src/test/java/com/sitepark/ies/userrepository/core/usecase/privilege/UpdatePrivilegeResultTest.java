package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeRoleAssignment;
import java.time.Instant;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdatePrivilegeResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdatePrivilegeResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdatePrivilegeResult.class).verify();
  }

  @Test
  void testHasPrivilegeChangesWithoutPatch() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1", "name", Instant.EPOCH, null, null, ReassignRolesToPrivilegesResult.skipped());

    assertFalse(result.hasPrivilegeChanges(), "no patch means no changes");
  }

  @Test
  void testHasPrivilegeChangesWithEmptyPatch() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(true),
            null,
            ReassignRolesToPrivilegesResult.skipped());

    assertFalse(result.hasPrivilegeChanges(), "empty patch means no changes");
  }

  @Test
  void testHasPrivilegeChangesWithPatch() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(false),
            null,
            ReassignRolesToPrivilegesResult.skipped());

    assertTrue(result.hasPrivilegeChanges(), "non-empty patch means changes");
  }

  @Test
  void testHasRoleChangesWhenSkipped() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1", "name", Instant.EPOCH, null, null, ReassignRolesToPrivilegesResult.skipped());

    assertFalse(result.hasRoleChanges(), "skipped reassignment means no changes");
  }

  @Test
  void testHasRoleChangesWhenNoReassignmentResult() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult("1", "name", Instant.EPOCH, null, null, null);

    assertFalse(result.hasRoleChanges(), "missing reassignment result means no changes");
  }

  @Test
  void testHasRoleChangesWhenReassigned() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1",
            "name",
            Instant.EPOCH,
            null,
            null,
            ReassignRolesToPrivilegesResult.reassigned(
                PrivilegeRoleAssignment.builder().build(),
                PrivilegeRoleAssignment.builder().build(),
                Instant.EPOCH));

    assertTrue(result.hasRoleChanges(), "reassignment means changes");
  }

  @Test
  void testHasAnyChangesWithoutChanges() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(true),
            null,
            ReassignRolesToPrivilegesResult.skipped());

    assertFalse(result.hasAnyChanges(), "nothing changed");
  }

  @Test
  void testHasAnyChangesWithPatchOnly() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1",
            "name",
            Instant.EPOCH,
            patch(false),
            null,
            ReassignRolesToPrivilegesResult.skipped());

    assertTrue(result.hasAnyChanges(), "patch is a change");
  }

  @Test
  void testHasAnyChangesWithReassignmentOnly() {
    UpdatePrivilegeResult result =
        new UpdatePrivilegeResult(
            "1",
            "name",
            Instant.EPOCH,
            null,
            null,
            ReassignRolesToPrivilegesResult.reassigned(
                PrivilegeRoleAssignment.builder().build(),
                PrivilegeRoleAssignment.builder().build(),
                Instant.EPOCH));

    assertTrue(result.hasAnyChanges(), "reassignment is a change");
  }

  private static PatchDocument patch(boolean empty) {
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(empty);
    return patch;
  }
}
