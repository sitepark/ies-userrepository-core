package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FinishUserRegistrationResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(FinishUserRegistrationResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(FinishUserRegistrationResult.class).verify();
  }
}
