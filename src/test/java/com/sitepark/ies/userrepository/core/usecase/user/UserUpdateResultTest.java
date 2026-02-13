package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UserUpdateResultTest {

  @Test
  void testEqualsUpdated() {
    EqualsVerifier.forClass(UserUpdateResult.Updated.class).verify();
  }

  @Test
  void testToStringUpdated() {
    ToStringVerifier.forClass(UserUpdateResult.Updated.class).verify();
  }

  @Test
  void testEqualsUnchanged() {
    EqualsVerifier.forClass(UserUpdateResult.Unchanged.class).verify();
  }

  @Test
  void testToStringUnchanged() {
    ToStringVerifier.forClass(UserUpdateResult.Unchanged.class).verify();
  }
}
