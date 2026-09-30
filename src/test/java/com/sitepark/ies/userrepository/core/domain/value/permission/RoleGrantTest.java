package com.sitepark.ies.userrepository.core.domain.value.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RoleGrantTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RoleGrant.class).verify();
  }

  @Test
  void testEmptyIsEmpty() {
    assertTrue(RoleGrant.builder().build().isEmpty(), "grant without permissions should be empty");
  }

  @Test
  void testNotEmptyWithCreate() {
    RoleGrant grant = RoleGrant.builder().create(true).build();

    assertFalse(grant.isEmpty(), "grant with create should not be empty");
  }

  @Test
  void testNotEmptyWithRead() {
    RoleGrant grant = RoleGrant.builder().read(true).build();

    assertFalse(grant.isEmpty(), "grant with read should not be empty");
  }

  @Test
  void testNotEmptyWithWrite() {
    RoleGrant grant = RoleGrant.builder().write(true).build();

    assertFalse(grant.isEmpty(), "grant with write should not be empty");
  }

  @Test
  void testNotEmptyWithDelete() {
    RoleGrant grant = RoleGrant.builder().delete(true).build();

    assertFalse(grant.isEmpty(), "grant with delete should not be empty");
  }

  @Test
  void testNotEmptyWithAssignPrivileges() {
    RoleGrant grant = RoleGrant.builder().assignPrivileges(true).build();

    assertFalse(grant.isEmpty(), "grant with assignPrivileges should not be empty");
  }

  @Test
  void testNotEmptyWithAllowedPrivilegeIds() {
    RoleGrant grant = RoleGrant.builder().allowedPrivilegeIds(List.of("1")).build();

    assertFalse(grant.isEmpty(), "grant with allowedPrivilegeIds should not be empty");
  }

  @Test
  void testToBuilderBuildEqualsOriginal() {
    RoleGrant grant =
        RoleGrant.builder()
            .create(true)
            .read(true)
            .write(true)
            .delete(true)
            .assignPrivileges(true)
            .allowedPrivilegeIds(List.of("1", "2"))
            .build();

    assertEquals(grant, grant.toBuilder().build(), "copy should equal the original");
  }

  @Test
  void testJsonEmptyFilterEqualsEmptyGrant() {
    assertEquals(
        new RoleGrant.JsonEmptyFilter(), RoleGrant.EMPTY, "filter should match an empty grant");
  }

  @Test
  void testJsonEmptyFilterNotEqualsNonEmptyGrant() {
    RoleGrant grant = RoleGrant.builder().read(true).build();

    assertNotEquals(
        new RoleGrant.JsonEmptyFilter(), grant, "filter should not match a non-empty grant");
  }

  @Test
  void testJsonEmptyFilterNotEqualsOtherType() {
    assertNotEquals(
        new RoleGrant.JsonEmptyFilter(), "other", "filter should not match another type");
  }

  @Test
  void testJsonEmptyFilterHashCodeIsStable() {
    assertEquals(
        new RoleGrant.JsonEmptyFilter().hashCode(),
        new RoleGrant.JsonEmptyFilter().hashCode(),
        "hash code should be stable across instances");
  }
}
