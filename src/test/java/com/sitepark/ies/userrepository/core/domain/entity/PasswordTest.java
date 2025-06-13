package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.userrepository.core.domain.value.Password;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PasswordTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Password.class).verify();
  }

  @Test
  void testSetHashAlgorithm() {
    Password password = Password.builder().hashAlgorithm("test").build();
    assertEquals("test", password.getHashAlgorithm(), "Hash algorithm should be set correctly");
  }

  @Test
  void testSetClearText() {
    Password password = Password.builder().clearText("test").build();
    assertEquals("test", password.getClearText(), "Clear text should be set correctly");
  }

  @Test
  void testSetHash() {
    Password password = Password.builder().hash("test").build();
    assertEquals("test", password.getHash(), "Hash should be set correctly");
  }

  @Test
  void testToBuilder() {
    Password password = Password.builder().hashAlgorithm("a").hash("b").clearText("c").build();
    Password copy = password.toBuilder().clearText("d").build();
    Password expected = Password.builder().hashAlgorithm("a").hash("b").clearText("d").build();
    assertEquals(expected, copy, "unexpected copy");
  }

  @Test
  void testToString() {
    Password password = Password.builder().hashAlgorithm("a").hash("b").clearText("c").build();
    assertEquals(
        "Password [hashAlgorithm=a, hash=******, clearText=******]",
        password.toString(),
        "unexpected toString");
  }

  @Test
  void testToStringWithNull() {
    Password password = Password.builder().hashAlgorithm("a").build();
    assertEquals(
        "Password [hashAlgorithm=a, hash=null, clearText=null]",
        password.toString(),
        "unexpected toString");
  }
}
