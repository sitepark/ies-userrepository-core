package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertPrivilegeRequestTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UpsertPrivilegeRequest.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UpsertPrivilegeRequest.class).verify();
  }
}
