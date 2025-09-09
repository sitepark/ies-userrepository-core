package com.sitepark.ies.userrepository.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

final class AddressTest {

  @Test
  void testEqualsContract() {
    EqualsVerifier.forClass(Address.class).verify();
  }

  @Test
  void testToStringContract() {
    ToStringVerifier.forClass(Address.class).verify();
  }

  @Test
  void testStreetField() {
    Address address = Address.builder().street("Mainstreet").build();
    assertEquals("Mainstreet", address.street(), "Street did not match");
  }

  @Test
  void testStreetFieldNull() {
    Address address = Address.builder().street(null).build();
    assertNull(address.street(), "Street is not null");
  }

  @Test
  void testHouseNumberField() {
    Address address = Address.builder().houseNumber("123A").build();
    assertEquals("123A", address.houseNumber(), "House number did not match");
  }

  @Test
  void testHouseNumberFieldNull() {
    Address address = Address.builder().houseNumber(null).build();
    assertNull(address.houseNumber(), "House number is not null");
  }

  @Test
  void testPostalCodeField() {
    Address address = Address.builder().postalCode("12345").build();
    assertEquals("12345", address.postalCode(), "Postal code did not match");
  }

  @Test
  void testPostalCodeFieldNull() {
    Address address = Address.builder().postalCode(null).build();
    assertNull(address.postalCode(), "Postal code is not null");
  }

  @Test
  void testCityField() {
    Address address = Address.builder().city("City").build();
    assertEquals("City", address.city(), "City did not match");
  }

  @Test
  void testCityFieldNull() {
    Address address = Address.builder().city(null).build();
    assertNull(address.city(), "City is not null");
  }
}
