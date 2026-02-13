package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdateRoleRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdateRoleRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdateRoleRequest.class).verify();
  }
}
