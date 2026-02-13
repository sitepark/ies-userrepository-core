package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UnassignRolesFromUsersRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UnassignRolesFromUsersRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UnassignRolesFromUsersRequest.class).verify();
  }
}
