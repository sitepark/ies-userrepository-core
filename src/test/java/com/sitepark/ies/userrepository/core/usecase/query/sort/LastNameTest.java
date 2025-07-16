package com.sitepark.ies.userrepository.core.usecase.query.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class LastNameTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(LastName.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(LastName.class).verify();
  }

  @Test
  void testConstructor() {
    LastName lastName = new LastName(Direction.ASC);
    assertEquals(Direction.ASC, lastName.getDirection(), "Unexpected direction");
  }
}
