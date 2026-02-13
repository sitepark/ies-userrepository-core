package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertPrivilegeResultTest {

  @Test
  void testEqualsCreated() {
    EqualsVerifier.forClass(UpsertPrivilegeResult.Created.class).verify();
  }

  @Test
  void testToStringCreated() {
    ToStringVerifier.forClass(UpsertPrivilegeResult.Created.class).verify();
  }

  @Test
  void testEqualsUpdated() {
    EqualsVerifier.forClass(UpsertPrivilegeResult.Updated.class).verify();
  }

  @Test
  void testToStringUpdated() {
    ToStringVerifier.forClass(UpsertPrivilegeResult.Updated.class).verify();
  }
}
