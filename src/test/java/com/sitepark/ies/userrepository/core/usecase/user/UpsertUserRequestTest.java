package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertUserRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpsertUserRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpsertUserRequest.class).verify();
  }
}
