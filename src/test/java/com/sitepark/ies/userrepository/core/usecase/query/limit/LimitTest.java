package com.sitepark.ies.userrepository.core.usecase.query.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LimitTest {

  @Test
  void testOffset() {
    OffsetLimit limit = Limit.offset(1, 2);
    assertEquals(1, limit.getOffset(), "Unexpected offset");
  }
}
