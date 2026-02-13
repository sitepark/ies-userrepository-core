package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FinishUserRegistrationRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(FinishUserRegistrationRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(FinishUserRegistrationRequest.class).verify();
  }
}
