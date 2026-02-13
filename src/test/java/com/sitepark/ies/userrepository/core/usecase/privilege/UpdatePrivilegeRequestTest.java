package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdatePrivilegeRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdatePrivilegeRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdatePrivilegeRequest.class).verify();
  }
}
