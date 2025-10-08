package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
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
  void testToBuilder() {
    Role role =
        Role.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();
    Role copy = role.toBuilder().description("description2").build();

    Role expected =
        Role.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description2")
            .build();

    assertEquals(expected, copy, "unexpected privileges");
  }

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);

    Role role =
        Role.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();

    String json = mapper.writeValueAsString(role);

    String expected =
        """
        {"anchor":"myanchor","name":"testrole","description":"description"}\
        """;

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());

    String json =
        """
        {"anchor":"myanchor","name":"testrole","description":"description"}\
        """;

    Role role = mapper.readValue(json, Role.class);

    Role expected =
        Role.builder()
            .name("testrole")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();

    assertEquals(expected, role, "unexpected role");
  }
}
