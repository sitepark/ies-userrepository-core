package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {
  @Test
  void testMessage() {
    UserNotFoundException exception = new UserNotFoundException("123");
    assertEquals("User with id 123 not found", exception.getMessage(), "unexpected message");
  }

  @Test
  void testId() {
    UserNotFoundException exception = new UserNotFoundException("123");
    assertEquals("123", exception.getId(), "unexpected id");
  }
}
