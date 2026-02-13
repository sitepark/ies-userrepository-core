package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CreatePrivilegeResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(CreatePrivilegeResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(CreatePrivilegeResult.class).verify();
  }
}
