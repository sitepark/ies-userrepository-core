package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemoveRoleRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RemoveRoleRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RemoveRoleRequest.class).verify();
  }
}
