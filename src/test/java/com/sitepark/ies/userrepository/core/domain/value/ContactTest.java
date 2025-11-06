package com.sitepark.ies.userrepository.core.domain.value;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(contact.phonePrivate())
        .withFailMessage("Private phone should match")
        .isEqualTo("0123456789");
  }

  @Test
  void testPhoneOffice() {
    Contact contact = Contact.builder().phoneOffice("0987654321").build();
    assertThat(contact.phoneOffice())
        .withFailMessage("Office phone should match")
        .isEqualTo("0987654321");
  }
}
