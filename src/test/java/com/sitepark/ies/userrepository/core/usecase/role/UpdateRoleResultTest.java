package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdateRoleResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdateRoleResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdateRoleResult.class).verify();
  }
}
