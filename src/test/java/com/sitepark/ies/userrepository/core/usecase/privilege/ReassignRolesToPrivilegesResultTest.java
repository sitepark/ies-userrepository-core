package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignRolesToPrivilegesResultTest {

  @Test
  void testEqualsReassigned() {
    EqualsVerifier.forClass(ReassignRolesToPrivilegesResult.Reassigned.class).verify();
  }

  @Test
  void testToStringReassigned() {
    ToStringVerifier.forClass(ReassignRolesToPrivilegesResult.Reassigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(ReassignRolesToPrivilegesResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(ReassignRolesToPrivilegesResult.Skipped.class).verify();
  }
}
