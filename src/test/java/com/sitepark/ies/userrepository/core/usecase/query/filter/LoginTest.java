package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class LoginTest {
  @Test
  void testEquals() {
    EqualsVerifier.forClass(Login.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(Login.class).verify();
  }
}
