package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RestoreUserResultTest {

  @Test
  void testEqualsRestored() {
    EqualsVerifier.forClass(RestoreUserResult.Restored.class).verify();
  }

  @Test
  void testToStringRestored() {
    ToStringVerifier.forClass(RestoreUserResult.Restored.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(RestoreUserResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(RestoreUserResult.Skipped.class).verify();
  }
}
