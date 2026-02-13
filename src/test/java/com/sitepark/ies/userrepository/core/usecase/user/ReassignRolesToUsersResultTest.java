package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ReassignRolesToUsersResultTest {

  @Test
  void testEqualsReassigned() {
    EqualsVerifier.forClass(ReassignRolesToUsersResult.Reassigned.class).verify();
  }

  @Test
  void testToStringReassigned() {
    ToStringVerifier.forClass(ReassignRolesToUsersResult.Reassigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(ReassignRolesToUsersResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(ReassignRolesToUsersResult.Skipped.class).verify();
  }
}
