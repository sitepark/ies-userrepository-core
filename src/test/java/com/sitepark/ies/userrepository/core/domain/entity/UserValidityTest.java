package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings({
  "PMD.JUnitTestContainsTooManyAsserts",
  "PMD.AvoidDuplicateLiterals",
  "PMD.TooManyMethods"
})
@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL",
  "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"
})
class UserValidityTest {

  private static final ZoneId ZONE_ID = ZoneId.of("Europe/Berlin");

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(UserValidity.class).verify();
  }

  @Test
  void testToBuilder() {

    OffsetDateTime validFrom =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
    OffsetDateTime validTo =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity =
        UserValidity.builder().blocked(false).validFrom(validFrom).validTo(validTo).build();

    UserValidity copyOfUserValidity = userValidity.toBuilder().build();

    assertFalse(copyOfUserValidity.isBlocked(), "blocked should be false");
    assertEquals(validFrom, userValidity.getValidFrom().get(), "unexpected validFrom");
    assertEquals(validTo, userValidity.getValidTo().get(), "unexpected validTo");
  }

  @Test
  void testToString() {

    OffsetDateTime validFrom =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
    OffsetDateTime validTo =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity =
        UserValidity.builder().blocked(false).validFrom(validFrom).validTo(validTo).build();

    String expecated =
        "UserValidity [blocked=false, validFrom=2023-08-21T00:00+02:00, "
            + "validTo=2023-08-21T00:00+02:00]";

    assertEquals(expecated, userValidity.toString(), "unexpected string representation");
  }

  @Test
  void testAlwaysValid() {

    UserValidity userValidity = UserValidity.ALWAYS_VALID;

    assertFalse(userValidity.isBlocked(), "blocked should be false");
    assertTrue(userValidity.getValidFrom().isEmpty(), "validFrom should be empty");
    assertTrue(userValidity.getValidTo().isEmpty(), "validTo should be empty");
  }

  @Test
  void testIsNowValid() {

    UserValidity userValidity = UserValidity.builder().blocked(false).build();

    assertTrue(userValidity.isNowValid(), "validity should be valid");
  }

  @Test
  void testIsValidWithNullBase() {
    assertThrows(
        NullPointerException.class,
        () -> {
          UserValidity.ALWAYS_VALID.isValid(null);
        });
  }

  @Test
  void testIsValidBlocked() {

    UserValidity userValidity = UserValidity.builder().blocked(true).build();

    OffsetDateTime base =
        LocalDate.of(2023, 8, 30).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    assertFalse(userValidity.isValid(base), "validity should't be valid");
  }

  @Test
  void testIsValidFrom() {

    OffsetDateTime validFrom =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    OffsetDateTime base =
        LocalDate.of(2023, 8, 30).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity = UserValidity.builder().validFrom(validFrom).build();

    assertTrue(userValidity.isValid(base), "validity should be valid");
  }

  @Test
  void testIsNotValidFrom() {

    OffsetDateTime validFrom =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    OffsetDateTime base =
        LocalDate.of(2023, 8, 20).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity = UserValidity.builder().validFrom(validFrom).build();

    assertFalse(userValidity.isValid(base), "validity should not be valid");
  }

  @Test
  void testIsValidTo() {

    OffsetDateTime validTo =
        LocalDate.of(2023, 8, 30).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    OffsetDateTime base =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity = UserValidity.builder().validTo(validTo).build();

    assertTrue(userValidity.isValid(base), "validity should be valid");
  }

  @Test
  void testIsNotValidTo() {

    OffsetDateTime validTo =
        LocalDate.of(2023, 8, 30).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    OffsetDateTime base =
        LocalDate.of(2023, 9, 1).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity = UserValidity.builder().validTo(validTo).build();

    assertFalse(userValidity.isValid(base), "validity should not be valid");
  }

  @Test
  void testSetValidFrom() {

    OffsetDateTime validFrom =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity = UserValidity.builder().validFrom(validFrom).build();

    assertFalse(userValidity.isBlocked(), "blocked should be false");
    assertEquals(validFrom, userValidity.getValidFrom().get(), "unexpected validFrom");
    assertTrue(userValidity.getValidTo().isEmpty(), "validTo should be empty");
  }

  @Test
  void testSetValidFromToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          UserValidity.builder().validFrom(null);
        });
  }

  @Test
  void testSetValidTo() {

    OffsetDateTime validTo =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    UserValidity userValidity = UserValidity.builder().validTo(validTo).build();

    assertFalse(userValidity.isBlocked(), "blocked should be false");
    assertTrue(userValidity.getValidFrom().isEmpty(), "validFrom should be empty");
    assertEquals(validTo, userValidity.getValidTo().get(), "unexpected validTo");
  }

  @Test
  void testSetValidToToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          UserValidity.builder().validTo(null);
        });
  }

  @Test
  void testSerialize() {

    JsonMapper mapper =
        JsonMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .changeDefaultPropertyInclusion(
                inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
            .build();

    UserValidity userValidity =
        UserValidity.builder()
            .blocked(false)
            .validFrom(LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime())
            .validTo(LocalDate.of(2023, 10, 1).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime())
            .build();

    String json = mapper.writeValueAsString(userValidity);

    String expected =
        """
        {\
        "blocked":false,\
        "validFrom":"2023-08-21T00:00:00+02:00",\
        "validTo":"2023-10-01T00:00:00+02:00"}\
        """;

    assertEquals(expected, json, "unexpected json");
  }
}
