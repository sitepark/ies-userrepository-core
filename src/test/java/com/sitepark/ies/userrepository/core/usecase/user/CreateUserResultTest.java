package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreateUserResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreateUserResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreateUserResult.class).verify();
  }
}
