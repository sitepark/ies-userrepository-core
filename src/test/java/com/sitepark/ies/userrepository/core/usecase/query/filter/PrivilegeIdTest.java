package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PrivilegeIdTest {
  @Test
  void testEquals() {
    EqualsVerifier.forClass(PrivilegeId.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(PrivilegeId.class).verify();
  }
}
