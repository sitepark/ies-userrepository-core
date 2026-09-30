package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import java.time.Instant;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdateUserResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdateUserResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdateUserResult.class).verify();
  }

  private static UserUpdateResult updated() {
    return UserUpdateResult.updated("name", mock(PatchDocument.class), mock(PatchDocument.class));
  }

  private static ReassignRolesToUsersResult reassigned() {
    return ReassignRolesToUsersResult.reassigned(
        UserRoleAssignment.builder().build(), UserRoleAssignment.builder().build(), Instant.EPOCH);
  }

  private static UpdateUserResult result(
      UserUpdateResult userResult, ReassignRolesToUsersResult roleResult) {
    return new UpdateUserResult("1", Instant.EPOCH, userResult, roleResult);
  }

  @Test
  void testHasUserChangesWhenUnchanged() {
    UpdateUserResult result =
        result(UserUpdateResult.unchanged(), ReassignRolesToUsersResult.skipped());

    assertFalse(result.hasUserChanges(), "unchanged user means no user changes");
  }

  @Test
  void testHasUserChangesWhenUpdated() {
    UpdateUserResult result = result(updated(), ReassignRolesToUsersResult.skipped());

    assertTrue(result.hasUserChanges(), "updated user means user changes");
  }

  @Test
  void testHasRoleChangesWhenSkipped() {
    UpdateUserResult result =
        result(UserUpdateResult.unchanged(), ReassignRolesToUsersResult.skipped());

    assertFalse(result.hasRoleChanges(), "skipped reassignment means no role changes");
  }

  @Test
  void testHasRoleChangesWhenReassigned() {
    UpdateUserResult result = result(UserUpdateResult.unchanged(), reassigned());

    assertTrue(result.hasRoleChanges(), "reassignment means role changes");
  }

  @Test
  void testHasAnyChangesWithoutChanges() {
    UpdateUserResult result =
        result(UserUpdateResult.unchanged(), ReassignRolesToUsersResult.skipped());

    assertFalse(result.hasAnyChanges(), "nothing changed");
  }

  @Test
  void testHasAnyChangesWithUserChangeOnly() {
    UpdateUserResult result = result(updated(), ReassignRolesToUsersResult.skipped());

    assertTrue(result.hasAnyChanges(), "user update is a change");
  }

  @Test
  void testHasAnyChangesWithRoleChangeOnly() {
    UpdateUserResult result = result(UserUpdateResult.unchanged(), reassigned());

    assertTrue(result.hasAnyChanges(), "role reassignment is a change");
  }

  @Test
  void testUserUpdateWhenUnchanged() {
    UpdateUserResult result =
        result(UserUpdateResult.unchanged(), ReassignRolesToUsersResult.skipped());

    assertNull(result.userUpdate(), "unchanged user has no update details");
  }

  @Test
  void testUserUpdateWhenUpdated() {
    UserUpdateResult updated = updated();
    UpdateUserResult result = result(updated, ReassignRolesToUsersResult.skipped());

    assertEquals(updated, result.userUpdate(), "updated user should expose its update details");
  }

  @Test
  void testRoleReassignmentResultWhenSkipped() {
    UpdateUserResult result =
        result(UserUpdateResult.unchanged(), ReassignRolesToUsersResult.skipped());

    assertNull(result.roleReassignmentResult(), "skipped reassignment is exposed as null");
  }

  @Test
  void testRoleReassignmentResultWhenReassigned() {
    ReassignRolesToUsersResult reassigned = reassigned();
    UpdateUserResult result = result(UserUpdateResult.unchanged(), reassigned);

    assertEquals(reassigned, result.roleReassignmentResult(), "reassignment should be exposed");
  }
}
