package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignUsersToRolesResultTest {

  @Test
  void testEqualsReassigned() {
    EqualsVerifier.forClass(ReassignUsersToRolesResult.Reassigned.class).verify();
  }

  @Test
  void testToStringReassigned() {
    ToStringVerifier.forClass(ReassignUsersToRolesResult.Reassigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(ReassignUsersToRolesResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(ReassignUsersToRolesResult.Skipped.class).verify();
  }
}
