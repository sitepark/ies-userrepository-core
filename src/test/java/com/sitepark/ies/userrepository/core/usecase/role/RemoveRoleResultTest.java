package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemoveRoleResultTest {

  @Test
  void testEqualsRemoved() {
    EqualsVerifier.forClass(RemoveRoleResult.Removed.class).verify();
  }

  @Test
  void testToStringRemoved() {
    ToStringVerifier.forClass(RemoveRoleResult.Removed.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(RemoveRoleResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(RemoveRoleResult.Skipped.class).verify();
  }
}
