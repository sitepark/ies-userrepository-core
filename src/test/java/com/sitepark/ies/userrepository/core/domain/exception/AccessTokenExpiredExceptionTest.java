package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class AccessTokenExpiredExceptionTest {
  @Test
  void testMessage() {
    OffsetDateTime now = OffsetDateTime.now();
    AccessTokenExpiredException exception = new AccessTokenExpiredException(now);
    assertEquals("Token has expired since " + now, exception.getMessage(), "unexpected message");
  }

  @Test
  void testExpiredAt() {
    OffsetDateTime now = OffsetDateTime.now();
    AccessTokenExpiredException exception = new AccessTokenExpiredException(now);
    assertEquals(now, exception.getExpiredAt(), "unexpected expiredAt");
  }
}
