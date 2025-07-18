package com.sitepark.ies.userrepository.core.usecase.query.sort;

import static org.junit.jupiter.api.Assertions.*;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreatedAtTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreatedAt.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreatedAt.class).verify();
  }

  @Test
  void testConstructor() {
    CreatedAt createdAt = new CreatedAt(Direction.ASC);
    assertEquals(Direction.ASC, createdAt.getDirection(), "Unexpected createdAt");
  }
}
