package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AssignPrivilegesToRolesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(AssignPrivilegesToRolesRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(AssignPrivilegesToRolesRequest.class).verify();
  }
}
