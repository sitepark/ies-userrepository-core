package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AssignPrivilegesToRolesResultTest {

  @Test
  void testEqualsAssigned() {
    EqualsVerifier.forClass(AssignPrivilegesToRolesResult.Assigned.class).verify();
  }

  @Test
  void testToStringAssigned() {
    ToStringVerifier.forClass(AssignPrivilegesToRolesResult.Assigned.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(AssignPrivilegesToRolesResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(AssignPrivilegesToRolesResult.Skipped.class).verify();
  }
}
