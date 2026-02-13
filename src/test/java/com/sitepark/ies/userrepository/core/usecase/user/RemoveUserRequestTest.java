package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemoveUserRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RemoveUserRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RemoveUserRequest.class).verify();
  }
}
