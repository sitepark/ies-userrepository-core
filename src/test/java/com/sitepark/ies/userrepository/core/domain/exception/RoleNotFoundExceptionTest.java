package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RoleNotFoundExceptionTest {
  @Test
  void testMessage() {
    RoleNotFoundException exception = new RoleNotFoundException("123");
    assertEquals("Role with id 123 not found", exception.getMessage(), "unexpected message");
  }

  @Test
  void testId() {
    RoleNotFoundException exception = new RoleNotFoundException("123");
    assertEquals("123", exception.getId(), "unexpected id");
  }
}
