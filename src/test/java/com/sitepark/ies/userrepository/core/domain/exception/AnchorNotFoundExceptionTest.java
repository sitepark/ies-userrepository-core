package com.sitepark.ies.userrepository.core.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import org.junit.jupiter.api.Test;

class AnchorNotFoundExceptionTest {

  @Test
  void testAnchor() {
    Anchor anchor = Anchor.ofString("abc");
    AnchorNotFoundException e = new AnchorNotFoundException(anchor);
    assertEquals(anchor, e.getAnchor(), "unexpected anchor");
  }

  @Test
  void testMessage() {
    Anchor anchor = Anchor.ofString("abc");
    AnchorNotFoundException e = new AnchorNotFoundException(anchor);
    assertEquals("Anchor abc not found", e.getMessage(), "unexpected message");
  }
}
