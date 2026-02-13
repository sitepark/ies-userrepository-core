package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignPrivilegesToRolesResultTest {

  @Test
  void testEqualsReassigned() {
    EqualsVerifier.forClass(ReassignPrivilegesToRolesResult.Reassigned.class).verify();
  }

  @Test
  void testToStringReassigned() {
    ToStringVerifier.forClass(ReassignPrivilegesToRolesResult.Reassigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(ReassignPrivilegesToRolesResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(ReassignPrivilegesToRolesResult.Skipped.class).verify();
  }
}
