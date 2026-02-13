package com.sitepark.ies.userrepository.core.usecase.role;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertRoleResultTest {

  @Test
  void testEqualsCreated() {
    EqualsVerifier.forClass(UpsertRoleResult.Created.class).verify();
  }

  @Test
  void testToStringCreated() {
    ToStringVerifier.forClass(UpsertRoleResult.Created.class).verify();
  }

  @Test
  void testEqualsUpdated() {
    EqualsVerifier.forClass(UpsertRoleResult.Updated.class).verify();
  }

  @Test
  void testToStringUpdated() {
    ToStringVerifier.forClass(UpsertRoleResult.Updated.class).verify();
  }
}
