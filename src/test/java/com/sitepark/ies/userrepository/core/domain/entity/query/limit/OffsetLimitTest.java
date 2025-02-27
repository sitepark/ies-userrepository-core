package com.sitepark.ies.userrepository.core.domain.entity.query.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import java.util.Optional;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class OffsetLimitTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(OffsetLimit.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testToString() {
    ToStringVerifier.forClass(OffsetLimit.class).verify();
  }

  @Test
  void testNullOffset() {
    OffsetLimit offsetLimit = new OffsetLimit(null, null);
    assertEquals(0, offsetLimit.getOffset(), "Offset should be 0");
  }

  @Test
  void testNullLimit() {
    OffsetLimit offsetLimit = new OffsetLimit(null, null);
    assertTrue(offsetLimit.getLimit().isEmpty(), "Limit should be empty");
  }

  @Test
  void testOffset() {
    OffsetLimit offsetLimit = new OffsetLimit(1, 2);
    assertEquals(1, offsetLimit.getOffset(), "Offset should be 1");
  }

  @Test
  void testLimit() {
    OffsetLimit offsetLimit = new OffsetLimit(1, 2);
    assertEquals(Optional.of(2), offsetLimit.getLimit(), "Limit should be 2");
  }
}
