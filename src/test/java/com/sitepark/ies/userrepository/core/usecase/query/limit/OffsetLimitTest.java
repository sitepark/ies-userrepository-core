package com.sitepark.ies.userrepository.core.usecase.query.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class OffsetLimitTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(OffsetLimit.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(OffsetLimit.class).verify();
  }

  @Test
  void testInvalidOffset() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new OffsetLimit(-1, 0);
        },
        "Offset must be >= 0, but was: -1");
  }

  @Test
  void testInvalidLimit() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new OffsetLimit(0, -1);
        },
        "Limit must be >= 0, but was: -1");
  }

  @Test
  void testOffset() {
    OffsetLimit offsetLimit = new OffsetLimit(1, 2);
    assertEquals(1, offsetLimit.getOffset(), "Offset should be 1");
  }

  @Test
  void testLimit() {
    OffsetLimit offsetLimit = new OffsetLimit(1, 2);
    assertEquals(2, offsetLimit.getLimit(), "Limit should be 2");
  }
}
