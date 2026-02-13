package com.sitepark.ies.userrepository.core.usecase.user;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertUserResultTest {

  @Test
  void testEqualsCreated() {
    EqualsVerifier.forClass(UpsertUserResult.Created.class).verify();
  }

  @Test
  void testToStringCreated() {
    ToStringVerifier.forClass(UpsertUserResult.Created.class).verify();
  }

  @Test
  void testEqualsUpdated() {
    EqualsVerifier.forClass(UpsertUserResult.Updated.class).verify();
  }

  @Test
  void testToStringUpdated() {
    ToStringVerifier.forClass(UpsertUserResult.Updated.class).verify();
  }
}
