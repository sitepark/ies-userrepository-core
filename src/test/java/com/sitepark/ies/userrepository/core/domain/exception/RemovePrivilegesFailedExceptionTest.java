package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RemovePrivilegesFailedExceptionTest {
  @Test
  void testConstructorWithMessage() {
    RemovePrivilegesFailedException exception = new RemovePrivilegesFailedException("test message");
    assertEquals("test message", exception.getMessage(), "unexpected message");
  }

  @Test
  void testConstructorWithMessageAndThrowable() {
    Throwable cause = new RuntimeException("cause");
    RemovePrivilegesFailedException exception =
        new RemovePrivilegesFailedException("test message", cause);
    assertEquals("test message", exception.getMessage(), "unexpected message");
  }

  @Test
  void testConstructorWithThrowableCause() {
    Throwable cause = new RuntimeException("cause");
    RemovePrivilegesFailedException exception =
        new RemovePrivilegesFailedException("test message", cause);
    assertEquals(cause, exception.getCause(), "unexpected cause");
  }
}
