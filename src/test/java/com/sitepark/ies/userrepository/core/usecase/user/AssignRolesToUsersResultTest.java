package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AssignRolesToUsersResultTest {

  @Test
  void testEqualsAssigned() {
    EqualsVerifier.forClass(AssignRolesToUsersResult.Assigned.class).verify();
  }

  @Test
  void testToStringAssigned() {
    ToStringVerifier.forClass(AssignRolesToUsersResult.Assigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(AssignRolesToUsersResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(AssignRolesToUsersResult.Skipped.class).verify();
  }
}
