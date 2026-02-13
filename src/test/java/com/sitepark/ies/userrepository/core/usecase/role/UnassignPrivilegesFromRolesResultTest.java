package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UnassignPrivilegesFromRolesResultTest {

  @Test
  void testEqualsUnassigned() {
    EqualsVerifier.forClass(UnassignPrivilegesFromRolesResult.Unassigned.class).verify();
  }

  @Test
  void testToStringUnassigned() {
    ToStringVerifier.forClass(UnassignPrivilegesFromRolesResult.Unassigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(UnassignPrivilegesFromRolesResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(UnassignPrivilegesFromRolesResult.Skipped.class).verify();
  }
}
