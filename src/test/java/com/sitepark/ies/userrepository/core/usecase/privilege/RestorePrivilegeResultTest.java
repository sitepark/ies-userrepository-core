package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RestorePrivilegeResultTest {

  @Test
  void testEqualsRestored() {
    EqualsVerifier.forClass(RestorePrivilegeResult.Restored.class).verify();
  }

  @Test
  void testToStringRestored() {
    ToStringVerifier.forClass(RestorePrivilegeResult.Restored.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(RestorePrivilegeResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(RestorePrivilegeResult.Skipped.class).verify();
  }
}
