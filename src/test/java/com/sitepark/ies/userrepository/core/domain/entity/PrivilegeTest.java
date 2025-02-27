package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PrivilegeTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Privilege.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testToString() {
    ToStringVerifier.forClass(Privilege.class).verify();
  }

  @Test
  void testName() {
    Privilege privilege = Privilege.builder().name("privilegerole").build();
    assertEquals("privilegerole", privilege.getName(), "unexpected name");
  }

  @Test
  void testWithoutName() {
    assertThrows(
        IllegalStateException.class,
        () -> Privilege.builder().build(),
        "Privileges without a name should not be allowed");
  }

  @Test
  void testId() {
    Privilege privilege = Privilege.builder().name("privilegerole").id("123").build();
    assertEquals("123", privilege.getId(), "unexpected id");
  }

  @Test
  void testWithInvalidId() {

    assertThrows(
        IllegalArgumentException.class,
        () -> Privilege.builder().name("privilegerole").id("a").build(),
        "id 0 should not be allowed");
  }

  @Test
  void testIdentifierWithId() {
    Privilege privilege =
        Privilege.builder().name("privilegerole").identifier(Identifier.ofId("123")).build();
    assertEquals("123", privilege.getId(), "unexpected id");
  }

  @Test
  void testIdentifierWithAnchor() {
    Privilege privilege =
        Privilege.builder()
            .name("privilegerole")
            .identifier(Identifier.ofAnchor("myanchor"))
            .build();
    assertEquals(Anchor.ofString("myanchor"), privilege.getAnchor(), "unexpected anchor");
  }

  @Test
  void testAnchorString() {
    Privilege privilege =
        Privilege.builder().name("privilegerole").anchor(Anchor.ofString("myanchor")).build();
    assertEquals(Anchor.ofString("myanchor"), privilege.getAnchor(), "unexpected anchor");
  }

  @Test
  void testAnchor() {
    Privilege privilege = Privilege.builder().name("privilegerole").anchor("myanchor").build();
    assertEquals(Anchor.ofString("myanchor"), privilege.getAnchor(), "unexpected anchor");
  }

  @Test
  void testDescription() {
    Privilege privilege =
        Privilege.builder().name("privilegerole").description("description").build();
    assertEquals("description", privilege.getDescription(), "unexpected description");
  }

  @Test
  void testEmptyDescription() {
    Privilege privilege = Privilege.builder().name("privilegerole").description("").build();
    assertNull(privilege.getDescription(), "unexpected description");
  }

  @Test
  void testNullDescription() {
    Privilege privilege = Privilege.builder().name("privilegerole").description(null).build();
    assertNull(privilege.getDescription(), "unexpected description");
  }

  @Test
  void testToBuilder() {
    Privilege privilege =
        Privilege.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();
    Privilege copy = privilege.toBuilder().description("description2").build();

    Privilege expected =
        Privilege.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description2")
            .build();

    assertEquals(expected, copy, "unexpected privileges");
  }
}
