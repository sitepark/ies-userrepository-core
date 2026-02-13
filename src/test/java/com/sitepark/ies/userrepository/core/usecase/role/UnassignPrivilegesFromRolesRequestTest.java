package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UnassignPrivilegesFromRolesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UnassignPrivilegesFromRolesRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UnassignPrivilegesFromRolesRequest.class).verify();
  }
}
