package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.List;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RoleTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(Role.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(Role.class).verify();
  }

  @Test
  void testName() {
    Role role = Role.builder().name("testrole").build();
    assertEquals("testrole", role.name(), "unexpected name");
  }

  @Test
  void testWithoutName() {
    assertThrows(
        IllegalStateException.class,
        () -> Role.builder().build(),
        "role without name should't be allowed");
  }

  @Test
  void testId() {
    Role role = Role.builder().name("testrole").id("123").build();
    assertEquals("123", role.id(), "unexpected id");
  }

  @Test
  void testWithInvalidId() {

    assertThrows(
        IllegalArgumentException.class,
        () -> Role.builder().name("testrole").id("a").build(),
        "id 0 should't be allowed");
  }

  @Test
  void testIdentifierWithId() {
    Role role = Role.builder().name("testrole").identifier(Identifier.ofId("123")).build();
    assertEquals("123", role.id(), "unexpected id");
  }

  @Test
  void testIdentifierWithAnchor() {
    Role role = Role.builder().name("testrole").identifier(Identifier.ofAnchor("myanchor")).build();
    assertEquals(Anchor.ofString("myanchor"), role.anchor(), "unexpected anchor");
  }

  @Test
  void testWithInvalidIdentifier() {
    Role role = Role.builder().name("testrole").identifier(Identifier.ofId("123")).build();
    assertEquals("123", role.id(), "unexpected id");
  }

  @Test
  void testAnchorString() {
    Role role = Role.builder().name("testrole").anchor(Anchor.ofString("myanchor")).build();
    assertEquals(Anchor.ofString("myanchor"), role.anchor(), "unexpected anchor");
  }

  @Test
  void testAnchor() {
    Role role = Role.builder().name("testrole").anchor("myanchor").build();
    assertEquals(Anchor.ofString("myanchor"), role.anchor(), "unexpected anchor");
  }

  @Test
  void testDescription() {
    Role role = Role.builder().name("testrole").description("description").build();
    assertEquals("description", role.description(), "unexpected description");
  }

  @Test
  void testEmptyDescription() {
    Role role = Role.builder().name("testrole").description("").build();
    assertNull(role.description(), "unexpected description");
  }

  @Test
  void testNullDescription() {
    Role role = Role.builder().name("testrole").description(null).build();
    assertNull(role.description(), "unexpected description");
  }

  @Test
  void testPrivilegesArray() {
    Role role = Role.builder().name("testrole").privilegeIds("123", "234").build();
    assertEquals(Set.of("123", "234"), role.privilegeIds(), "unexpected privileges");
  }

  @Test
  void testPrivilegesList() {
    Role role = Role.builder().name("testrole").privilegeIds(List.of("123", "234")).build();
    assertEquals(Set.of("123", "234"), role.privilegeIds(), "unexpected privileges");
  }

  @Test
  void testPrivilege() {
    Role role = Role.builder().name("testrole").privilegeId("123").privilegeId("234").build();
    assertEquals(Set.of("123", "234"), role.privilegeIds(), "unexpected privileges");
  }

  @Test
  void testToBuilder() {
    Role role =
        Role.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .privilegeId("123")
            .privilegeId("234")
            .build();
    Role copy = role.toBuilder().description("description2").build();

    Role expected =
        Role.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description2")
            .privilegeId("123")
            .privilegeId("234")
            .build();

    assertEquals(expected, copy, "unexpected privileges");
  }
}
