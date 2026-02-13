package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemovePrivilegeResultTest {

  @Test
  void testEqualsRemoved() {
    EqualsVerifier.forClass(RemovePrivilegeResult.Removed.class).verify();
  }

  @Test
  void testToStringRemoved() {
    ToStringVerifier.forClass(RemovePrivilegeResult.Removed.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(RemovePrivilegeResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(RemovePrivilegeResult.Skipped.class).verify();
  }
}
