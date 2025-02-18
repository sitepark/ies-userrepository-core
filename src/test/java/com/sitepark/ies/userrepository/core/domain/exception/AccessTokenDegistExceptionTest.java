package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccessTokenDegistExceptionTest {

  @Test
  void testWithMessage() {
    AccessTokenDegistException e = new AccessTokenDegistException("message");
    assertEquals("message", e.getMessage(), "Unexpected message");
  }

  @Test
  void testWithCause() {
    Exception cause = new Exception();
    AccessTokenDegistException e = new AccessTokenDegistException(null, cause);
    assertEquals(cause, e.getCause(), "Unexpected cause");
  }
}
