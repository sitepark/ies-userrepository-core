package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UnassignPrivilegesFromRolesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UnassignPrivilegesFromRolesRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UnassignPrivilegesFromRolesRequest.class).verify();
  }

  @Test
  void testIsEmptyWithoutIdentifiers() {
    assertTrue(
        UnassignPrivilegesFromRolesRequest.builder().build().isEmpty(),
        "request without identifiers should be empty");
  }

  @Test
  void testIsEmptyWithoutRoles() {
    UnassignPrivilegesFromRolesRequest request =
        UnassignPrivilegesFromRolesRequest.builder().privilegeIdentifiers(b -> b.id("1")).build();

    assertTrue(request.isEmpty(), "request without role identifiers should be empty");
  }

  @Test
  void testIsEmptyWithoutPrivileges() {
    UnassignPrivilegesFromRolesRequest request =
        UnassignPrivilegesFromRolesRequest.builder().roleIdentifiers(b -> b.id("1")).build();

    assertTrue(request.isEmpty(), "request without privilege identifiers should be empty");
  }

  @Test
  void testIsNotEmptyWithBothSides() {
    UnassignPrivilegesFromRolesRequest request =
        UnassignPrivilegesFromRolesRequest.builder()
            .roleIdentifiers(b -> b.id("1"))
            .privilegeIdentifiers(b -> b.id("2"))
            .build();

    assertFalse(request.isEmpty(), "request with both identifier lists should not be empty");
  }

  @Test
  void testToBuilderBuildEqualsOriginal() {
    UnassignPrivilegesFromRolesRequest request =
        UnassignPrivilegesFromRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .privilegeIdentifiers(b -> b.ids("3", "4"))
            .build();

    assertEquals(request, request.toBuilder().build(), "copy should equal the original");
  }
}
