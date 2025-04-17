package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccessTokenDigestExceptionTest {

  @Test
  void testWithMessage() {
    AccessTokenDigestException e = new AccessTokenDigestException("message");
    assertEquals("message", e.getMessage(), "Unexpected message");
  }

  @Test
  void testWithCause() {
    Exception cause = new Exception();
    AccessTokenDigestException e = new AccessTokenDigestException(null, cause);
    assertEquals(cause, e.getCause(), "Unexpected cause");
  }
}
