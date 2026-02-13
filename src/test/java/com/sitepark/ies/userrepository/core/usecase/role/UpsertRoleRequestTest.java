package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertRoleRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpsertRoleRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpsertRoleRequest.class).verify();
  }
}
