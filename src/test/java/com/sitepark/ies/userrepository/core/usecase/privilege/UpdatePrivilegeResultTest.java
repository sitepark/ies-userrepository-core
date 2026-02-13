package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpdatePrivilegeResultTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpdatePrivilegeResult.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpdatePrivilegeResult.class).verify();
  }
}
