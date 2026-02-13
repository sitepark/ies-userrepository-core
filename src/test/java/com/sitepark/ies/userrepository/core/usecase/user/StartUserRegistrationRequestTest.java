package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class StartUserRegistrationRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(StartUserRegistrationRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(StartUserRegistrationRequest.class).verify();
  }
}
