package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RestoreRoleResultTest {

  @Test
  void testEqualsRestored() {
    EqualsVerifier.forClass(RestoreRoleResult.Restored.class).verify();
  }

  @Test
  void testToStringRestored() {
    ToStringVerifier.forClass(RestoreRoleResult.Restored.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(RestoreRoleResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(RestoreRoleResult.Skipped.class).verify();
  }
}
