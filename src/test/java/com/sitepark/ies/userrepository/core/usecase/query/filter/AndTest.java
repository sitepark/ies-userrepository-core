package com.sitepark.ies.userrepository.core.usecase.query.filter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AndTest {
  @Test
  void testEquals() {
    EqualsVerifier.forClass(And.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(And.class).verify();
  }

  @Test
  void testEmpty() {
    assertThrows(IllegalArgumentException.class, And::new, "And should not be empty");
  }
}
