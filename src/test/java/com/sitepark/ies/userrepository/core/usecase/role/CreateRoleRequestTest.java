package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreateRoleRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreateRoleRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreateRoleRequest.class).verify();
  }
}
