package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UnassignRolesFromUsersResultTest {

  @Test
  void testEqualsUnassigned() {
    EqualsVerifier.forClass(UnassignRolesFromUsersResult.Unassigned.class).verify();
  }

  @Test
  void testToStringUnassigned() {
    ToStringVerifier.forClass(UnassignRolesFromUsersResult.Unassigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(UnassignRolesFromUsersResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(UnassignRolesFromUsersResult.Skipped.class).verify();
  }
}
