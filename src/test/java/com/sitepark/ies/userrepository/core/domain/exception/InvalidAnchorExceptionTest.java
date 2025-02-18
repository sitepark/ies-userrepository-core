package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvalidAnchorExceptionTest {

  @Test
  void testGetMessageWithNullMessage() {
    InvalidAnchorException e = new InvalidAnchorException("anchor", null);
    assertEquals("Invalid anchor 'anchor'", e.getMessage(), "Unexpected message");
  }
}
