package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignUsersToRolesRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(ReassignUsersToRolesRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(ReassignUsersToRolesRequest.class).verify();
  }
}
