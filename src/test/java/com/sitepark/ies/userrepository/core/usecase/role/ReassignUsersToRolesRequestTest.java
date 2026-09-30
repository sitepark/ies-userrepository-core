package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignUsersToRolesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(ReassignUsersToRolesRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(ReassignUsersToRolesRequest.class).verify();
  }

  @Test
  void testIsEmptyWithoutIdentifiers() {
    assertTrue(
        ReassignUsersToRolesRequest.builder().build().isEmpty(),
        "request without identifiers should be empty");
  }

  @Test
  void testIsEmptyWithoutRoles() {
    ReassignUsersToRolesRequest request =
        ReassignUsersToRolesRequest.builder().userIdentifiers(b -> b.id("1")).build();

    assertTrue(request.isEmpty(), "request without role identifiers should be empty");
  }

  @Test
  void testIsEmptyWithoutUsers() {
    ReassignUsersToRolesRequest request =
        ReassignUsersToRolesRequest.builder().roleIdentifiers(b -> b.id("1")).build();

    assertTrue(request.isEmpty(), "request without user identifiers should be empty");
  }

  @Test
  void testIsNotEmptyWithBothSides() {
    ReassignUsersToRolesRequest request =
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.id("1"))
            .userIdentifiers(b -> b.id("2"))
            .build();

    assertFalse(request.isEmpty(), "request with both identifier lists should not be empty");
  }

  @Test
  void testToBuilderBuildEqualsOriginal() {
    ReassignUsersToRolesRequest request =
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build();

    assertEquals(request, request.toBuilder().build(), "copy should equal the original");
  }
}
