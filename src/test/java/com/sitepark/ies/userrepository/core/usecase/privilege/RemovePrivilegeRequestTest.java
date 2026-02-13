package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemovePrivilegeRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RemovePrivilegeRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RemovePrivilegeRequest.class).verify();
  }
}
