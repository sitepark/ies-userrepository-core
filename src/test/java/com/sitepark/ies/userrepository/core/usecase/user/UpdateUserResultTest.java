package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdateUserResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdateUserResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdateUserResult.class).verify();
  }
}
