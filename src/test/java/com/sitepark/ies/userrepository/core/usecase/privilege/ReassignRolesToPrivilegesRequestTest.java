package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignRolesToPrivilegesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(ReassignRolesToPrivilegesRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(ReassignRolesToPrivilegesRequest.class).verify();
  }
}
