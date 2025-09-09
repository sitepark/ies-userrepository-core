package com.sitepark.ies.userrepository.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

final class OrganisationTest {

  @Test
  void testEqualsContract() {
    EqualsVerifier.forClass(Organisation.class).verify();
  }

  @Test
  void testToStringContract() {
    ToStringVerifier.forClass(Organisation.class).verify();
  }

  @Test
  void testName() {
    Organisation organisation = Organisation.builder().name("Organistaion").build();
    assertEquals("Organistaion", organisation.name(), "Organistaion should match");
  }
}
