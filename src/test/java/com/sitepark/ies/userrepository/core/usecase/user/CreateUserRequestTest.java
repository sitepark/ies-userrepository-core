package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreateUserRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreateUserRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreateUserRequest.class).verify();
  }
}
