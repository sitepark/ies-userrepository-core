package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.PermissionPayload;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PrivilegeTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(Privilege.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(Privilege.class).verify();
  }

  @Test
  void testName() {
    Privilege privilege = Privilege.builder().name("privilegerole").build();
    assertEquals("privilegerole", privilege.name(), "unexpected name");
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
    assertEquals("123", privilege.id(), "unexpected id");
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
    assertEquals("123", privilege.id(), "unexpected id");
  }

  @Test
  void testIdentifierWithAnchor() {
    Privilege privilege =
        Privilege.builder()
            .name("privilegerole")
            .identifier(Identifier.ofAnchor("myanchor"))
            .build();
    assertEquals(Anchor.ofString("myanchor"), privilege.anchor(), "unexpected anchor");
  }

  @Test
  void testAnchorString() {
    Privilege privilege =
        Privilege.builder().name("privilegerole").anchor(Anchor.ofString("myanchor")).build();
    assertEquals(Anchor.ofString("myanchor"), privilege.anchor(), "unexpected anchor");
  }

  @Test
  void testAnchor() {
    Privilege privilege = Privilege.builder().name("privilegerole").anchor("myanchor").build();
    assertEquals(Anchor.ofString("myanchor"), privilege.anchor(), "unexpected anchor");
  }

  @Test
  void testDescription() {
    Privilege privilege =
        Privilege.builder().name("privilegerole").description("description").build();
    assertEquals("description", privilege.description(), "unexpected description");
  }

  @Test
  void testEmptyDescription() {
    Privilege privilege = Privilege.builder().name("privilegerole").description("").build();
    assertNull(privilege.description(), "unexpected description");
  }

  @Test
  void testNullDescription() {
    Privilege privilege = Privilege.builder().name("privilegerole").description(null).build();
    assertNull(privilege.description(), "unexpected description");
  }

  @Test
  void testPermission() {
    PermissionPayload permission = mock();
    Privilege privilege = Privilege.builder().name("privilegerole").permission(permission).build();
    assertSame(permission, privilege.permission(), "unexpected permission");
  }

  @Test
  void testToBuilder() {
    Privilege privilege =
        Privilege.builder()
            .name("testprivilege")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();
    Privilege copy = privilege.toBuilder().description("description2").build();

    Privilege expected =
        Privilege.builder()
            .name("testprivilege")
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

    Privilege privilege =
        Privilege.builder()
            .name("testprivilege")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();

    String json = mapper.writeValueAsString(privilege);

    String expected =
        """
        {"anchor":"myanchor","name":"testprivilege","description":"description"}\
        """;
    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());

    String json =
        """
        {"anchor":"myanchor","name":"testprivilege","description":"description"}\
        """;

    Privilege privilege = mapper.readValue(json, Privilege.class);

    Privilege expected =
        Privilege.builder()
            .name("testprivilege")
            .anchor(Anchor.ofString("myanchor"))
            .description("description")
            .build();

    assertEquals(expected, privilege, "unexpected privilege");
  }
}
