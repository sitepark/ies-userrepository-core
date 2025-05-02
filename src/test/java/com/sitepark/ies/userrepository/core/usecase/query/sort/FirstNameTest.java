package com.sitepark.ies.userrepository.core.usecase.query.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FirstNameTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(FirstName.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testToString() {
    ToStringVerifier.forClass(FirstName.class).verify();
  }

  @Test
  void testConstructor() {
    FirstName firstName = new FirstName(Direction.ASC);
    assertEquals(Direction.ASC, firstName.getDirection(), "Unexpected direction");
  }
}
