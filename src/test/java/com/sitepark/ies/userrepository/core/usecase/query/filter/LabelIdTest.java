package com.sitepark.ies.userrepository.core.usecase.query.filter;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class LabelIdTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(LabelId.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(LabelId.class).verify();
  }
}
