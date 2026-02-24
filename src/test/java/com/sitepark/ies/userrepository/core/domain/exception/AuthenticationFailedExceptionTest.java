package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serial;
import org.junit.jupiter.api.Test;

class AuthenticationFailedExceptionTest {

  private static class ConcreteAuthFailedException extends AuthenticationFailedException {
    @Serial private static final long serialVersionUID = 1L;

    ConcreteAuthFailedException() {}

    ConcreteAuthFailedException(String msg) {
      super(msg);
    }
  }

  @Test
  void testConstructorWithoutMessage() {
    ConcreteAuthFailedException exception = new ConcreteAuthFailedException();
    assertNull(exception.getMessage(), "No-arg constructor should produce null message");
  }

  @Test
  void testConstructorWithMessage() {
    ConcreteAuthFailedException exception =
        new ConcreteAuthFailedException("authentication failed");
    assertEquals("authentication failed", exception.getMessage(), "Unexpected exception message");
  }
}
