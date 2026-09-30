package com.sitepark.ies.userrepository.core.domain.value.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PrivilegeGrantTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(PrivilegeGrant.class).verify();
  }

  @Test
  void testEmptyIsEmpty() {
    assertTrue(
        PrivilegeGrant.builder().build().isEmpty(), "grant without permissions should be empty");
  }

  @Test
  void testNotEmptyWithCreate() {
    PrivilegeGrant grant = PrivilegeGrant.builder().create(true).build();

    assertFalse(grant.isEmpty(), "grant with create should not be empty");
  }

  @Test
  void testNotEmptyWithRead() {
    PrivilegeGrant grant = PrivilegeGrant.builder().read(true).build();

    assertFalse(grant.isEmpty(), "grant with read should not be empty");
  }

  @Test
  void testNotEmptyWithWrite() {
    PrivilegeGrant grant = PrivilegeGrant.builder().write(true).build();

    assertFalse(grant.isEmpty(), "grant with write should not be empty");
  }

  @Test
  void testNotEmptyWithDelete() {
    PrivilegeGrant grant = PrivilegeGrant.builder().delete(true).build();

    assertFalse(grant.isEmpty(), "grant with delete should not be empty");
  }

  @Test
  void testToBuilderBuildEqualsOriginal() {
    PrivilegeGrant grant =
        PrivilegeGrant.builder().create(true).read(true).write(true).delete(true).build();

    assertEquals(grant, grant.toBuilder().build(), "copy should equal the original");
  }

  @Test
  void testJsonEmptyFilterEqualsEmptyGrant() {
    assertEquals(
        new PrivilegeGrant.JsonEmptyFilter(),
        PrivilegeGrant.EMPTY,
        "filter should match an empty grant");
  }

  @Test
  void testJsonEmptyFilterNotEqualsNonEmptyGrant() {
    PrivilegeGrant grant = PrivilegeGrant.builder().read(true).build();

    assertNotEquals(
        new PrivilegeGrant.JsonEmptyFilter(), grant, "filter should not match a non-empty grant");
  }

  @Test
  void testJsonEmptyFilterNotEqualsOtherType() {
    assertNotEquals(
        new PrivilegeGrant.JsonEmptyFilter(), "other", "filter should not match another type");
  }

  @Test
  void testJsonEmptyFilterHashCodeIsStable() {
    assertEquals(
        new PrivilegeGrant.JsonEmptyFilter().hashCode(),
        new PrivilegeGrant.JsonEmptyFilter().hashCode(),
        "hash code should be stable across instances");
  }
}
