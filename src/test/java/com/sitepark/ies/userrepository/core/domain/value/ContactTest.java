package com.sitepark.ies.userrepository.core.domain.value;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

final class ContactTest {

  @Test
  void testEqualsContract() {
    EqualsVerifier.forClass(Contact.class).verify();
  }

  @Test
  void testToStringContract() {
    ToStringVerifier.forClass(Contact.class).verify();
  }

  @Test
  void testPhonePrivate() {
    Contact contact = Contact.builder().phonePrivate("0123456789").build();
    assertThat("Private phone should match", contact.phonePrivate(), is("0123456789"));
  }

  @Test
  void testPhoneOffice() {
    Contact contact = Contact.builder().phoneOffice("0987654321").build();
    assertThat("Office phone should match", contact.phoneOffice(), is("0987654321"));
  }
}
