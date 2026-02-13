package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdateUserRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdateUserRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdateUserRequest.class).verify();
  }
}
