package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignRolesToUsersRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(ReassignRolesToUsersRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(ReassignRolesToUsersRequest.class).verify();
  }

  @Test
  void testIsEmptyWithoutIdentifiers() {
    assertTrue(
        ReassignRolesToUsersRequest.builder().build().isEmpty(),
        "request without identifiers should be empty");
  }

  @Test
  void testIsEmptyWithoutRoles() {
    ReassignRolesToUsersRequest request =
        ReassignRolesToUsersRequest.builder().userIdentifiers(b -> b.id("1")).build();

    assertTrue(request.isEmpty(), "request without role identifiers should be empty");
  }

  @Test
  void testIsEmptyWithoutUsers() {
    ReassignRolesToUsersRequest request =
        ReassignRolesToUsersRequest.builder().roleIdentifiers(b -> b.id("1")).build();

    assertTrue(request.isEmpty(), "request without user identifiers should be empty");
  }

  @Test
  void testIsNotEmptyWithBothSides() {
    ReassignRolesToUsersRequest request =
        ReassignRolesToUsersRequest.builder()
            .roleIdentifiers(b -> b.id("1"))
            .userIdentifiers(b -> b.id("2"))
            .build();

    assertFalse(request.isEmpty(), "request with both identifier lists should not be empty");
  }

  @Test
  void testToBuilderBuildEqualsOriginal() {
    ReassignRolesToUsersRequest request =
        ReassignRolesToUsersRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build();

    assertEquals(request, request.toBuilder().build(), "copy should equal the original");
  }
}
