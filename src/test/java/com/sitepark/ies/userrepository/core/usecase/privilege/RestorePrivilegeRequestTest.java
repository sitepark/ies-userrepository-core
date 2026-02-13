package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RestorePrivilegeRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RestorePrivilegeRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RestorePrivilegeRequest.class).verify();
  }
}
