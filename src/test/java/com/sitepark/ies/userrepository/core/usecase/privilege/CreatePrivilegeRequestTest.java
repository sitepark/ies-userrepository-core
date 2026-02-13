package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreatePrivilegeRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreatePrivilegeRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreatePrivilegeRequest.class).verify();
  }
}
