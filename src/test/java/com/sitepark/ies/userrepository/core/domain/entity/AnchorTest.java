package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAnchorException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class AnchorTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Anchor.class).verify();
  }

  @Test
  void testOfNullString() {
    Anchor anchor = Anchor.ofString(null);
    assertNull(anchor, "anchor should be null");
  }

  @Test
  void testOfBlankString() {
    Anchor anchor = Anchor.ofString("  ");
    assertEquals(Anchor.EMPTY, anchor, "anchor should be Anchor.EMPTY");
  }

  @Test
  void testValidateValidAnchor() {
    assertDoesNotThrow(
        () -> {
          Anchor.ofString("123a");
        });
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testValidateOnlyDigits() {
    InvalidAnchorException e =
        assertThrows(
            InvalidAnchorException.class,
            () -> {
              Anchor.ofString("1234556789012345");
            },
            "anchor must not only contain numbers");
    assertEquals("1234556789012345", e.getName(), "unexpected name");
    assertNotNull(e.getMessage(), "message is null");
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testValidateInvalidChars() {
    InvalidAnchorException e =
        assertThrows(
            InvalidAnchorException.class,
            () -> {
              Anchor.ofString("a.b,c");
            },
            "anchor must not contain commas");
    assertEquals("a.b,c", e.getName(), "unexpected name");
    assertNotNull(e.getMessage(), "message is null");
  }

  @Test
  void testToString() {
    Anchor anchor = Anchor.ofString("a.b.c");
    assertEquals("a.b.c", anchor.toString(), "unexpected string representation");
  }

  @Test
  void testEmptyAnchorToString() {
    Anchor anchor = Anchor.EMPTY;
    assertEquals("EMPTY", anchor.toString(), "unexpected string representation");
  }

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();

    Anchor anchor = Anchor.ofString("abc");

    String json = mapper.writeValueAsString(anchor);

    assertEquals("\"abc\"", json, "unexpected value");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();

    String json = "\"abc\"";

    Anchor anchor = mapper.readValue(json, Anchor.class);

    assertEquals("abc", anchor.getName(), "unexpected anchor");
  }
}
