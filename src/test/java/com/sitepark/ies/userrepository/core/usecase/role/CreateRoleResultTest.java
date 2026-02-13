package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreateRoleResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreateRoleResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreateRoleResult.class).verify();
  }
}
