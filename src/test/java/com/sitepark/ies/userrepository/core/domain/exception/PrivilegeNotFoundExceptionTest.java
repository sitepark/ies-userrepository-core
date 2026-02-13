package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PrivilegeNotFoundExceptionTest {
  @Test
  void testMessage() {
    PrivilegeNotFoundException exception = new PrivilegeNotFoundException("456");
    assertEquals("Privilege with id 456 not found", exception.getMessage(), "unexpected message");
  }

  @Test
  void testId() {
    PrivilegeNotFoundException exception = new PrivilegeNotFoundException("456");
    assertEquals("456", exception.getId(), "unexpected id");
  }
}
