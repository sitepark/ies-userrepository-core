package com.sitepark.ies.userrepository.core.domain.value.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UserGrantTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UserGrant.class).verify();
  }

  @Test
  void testEmptyIsEmpty() {
    assertTrue(UserGrant.builder().build().isEmpty(), "grant without permissions should be empty");
  }

  @Test
  void testNotEmptyWithCreate() {
    UserGrant grant = UserGrant.builder().create(true).build();

    assertFalse(grant.isEmpty(), "grant with create should not be empty");
  }

  @Test
  void testNotEmptyWithRead() {
    UserGrant grant = UserGrant.builder().read(true).build();

    assertFalse(grant.isEmpty(), "grant with read should not be empty");
  }

  @Test
  void testNotEmptyWithWrite() {
    UserGrant grant = UserGrant.builder().write(true).build();

    assertFalse(grant.isEmpty(), "grant with write should not be empty");
  }

  @Test
  void testNotEmptyWithDelete() {
    UserGrant grant = UserGrant.builder().delete(true).build();

    assertFalse(grant.isEmpty(), "grant with delete should not be empty");
  }

  @Test
  void testNotEmptyWithAssignRoles() {
    UserGrant grant = UserGrant.builder().assignRoles(true).build();

    assertFalse(grant.isEmpty(), "grant with assignRoles should not be empty");
  }

  @Test
  void testNotEmptyWithAllowedRoleIds() {
    UserGrant grant = UserGrant.builder().allowedRoleIds(List.of("1")).build();

    assertFalse(grant.isEmpty(), "grant with allowedRoleIds should not be empty");
  }

  @Test
  void testToBuilderBuildEqualsOriginal() {
    UserGrant grant =
        UserGrant.builder()
            .create(true)
            .read(true)
            .write(true)
            .delete(true)
            .assignRoles(true)
            .allowedRoleIds(List.of("1", "2"))
            .build();

    assertEquals(grant, grant.toBuilder().build(), "copy should equal the original");
  }

  @Test
  void testJsonEmptyFilterEqualsEmptyGrant() {
    assertEquals(
        new UserGrant.JsonEmptyFilter(), UserGrant.EMPTY, "filter should match an empty grant");
  }

  @Test
  void testJsonEmptyFilterNotEqualsNonEmptyGrant() {
    UserGrant grant = UserGrant.builder().read(true).build();

    assertNotEquals(
        new UserGrant.JsonEmptyFilter(), grant, "filter should not match a non-empty grant");
  }

  @Test
  void testJsonEmptyFilterNotEqualsOtherType() {
    assertNotEquals(
        new UserGrant.JsonEmptyFilter(), "other", "filter should not match another type");
  }

  @Test
  void testJsonEmptyFilterHashCodeIsStable() {
    assertEquals(
        new UserGrant.JsonEmptyFilter().hashCode(),
        new UserGrant.JsonEmptyFilter().hashCode(),
        "hash code should be stable across instances");
  }
}
