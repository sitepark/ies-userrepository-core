package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class StartUserRegistrationResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(StartUserRegistrationResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(StartUserRegistrationResult.class).verify();
  }
}
