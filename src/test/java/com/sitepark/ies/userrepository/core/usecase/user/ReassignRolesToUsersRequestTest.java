package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignRolesToUsersRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(ReassignRolesToUsersRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(ReassignRolesToUsersRequest.class).verify();
  }
}
