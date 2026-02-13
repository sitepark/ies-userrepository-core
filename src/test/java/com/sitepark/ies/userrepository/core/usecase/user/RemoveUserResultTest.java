package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RemoveUserResultTest {

  @Test
  void testEqualsRemoved() {
    EqualsVerifier.forClass(RemoveUserResult.Removed.class).verify();
  }

  @Test
  void testToStringRemoved() {
    ToStringVerifier.forClass(RemoveUserResult.Removed.class).verify();
  }

  @Test
  void testEqualsSkipped() {
    EqualsVerifier.forClass(RemoveUserResult.Skipped.class).verify();
  }

  @Test
  void testToStringSkipped() {
    ToStringVerifier.forClass(RemoveUserResult.Skipped.class).verify();
  }
}
