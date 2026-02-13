package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AssignRolesToUsersRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(AssignRolesToUsersRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(AssignRolesToUsersRequest.class).verify();
  }
}
