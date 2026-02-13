package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UpdatePrivilegeFailedExceptionTest {
  @Test
  void testConstructorWithMessage() {
    UpdatePrivilegeFailedException exception = new UpdatePrivilegeFailedException("test message");
    assertEquals("test message", exception.getMessage(), "unexpected message");
  }

  @Test
  void testConstructorWithMessageAndThrowable() {
    Throwable cause = new RuntimeException("cause");
    UpdatePrivilegeFailedException exception =
        new UpdatePrivilegeFailedException("test message", cause);
    assertEquals("test message", exception.getMessage(), "unexpected message");
  }

  @Test
  void testConstructorWithThrowableCause() {
    Throwable cause = new RuntimeException("cause");
    UpdatePrivilegeFailedException exception =
        new UpdatePrivilegeFailedException("test message", cause);
    assertEquals(cause, exception.getCause(), "unexpected cause");
  }
}
