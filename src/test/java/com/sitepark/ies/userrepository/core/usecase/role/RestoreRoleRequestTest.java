package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RestoreRoleRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RestoreRoleRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RestoreRoleRequest.class).verify();
  }
}
