package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RoleIdListTest {
  @Test
  void testEquals() {
    EqualsVerifier.forClass(RoleIdList.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RoleIdList.class).verify();
  }
}
