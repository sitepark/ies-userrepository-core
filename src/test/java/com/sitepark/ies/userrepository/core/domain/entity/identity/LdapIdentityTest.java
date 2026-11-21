package com.sitepark.ies.userrepository.core.domain.entity.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings("PMD.TooManyMethods")
@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL",
  "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"
})
class LdapIdentityTest {

  private static final String USER_DN = "userdn";

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(LdapIdentity.class).verify();
  }

  @Test
  void testSetServer() {
    LdapIdentity ldapIdentity = LdapIdentity.builder().server(2).dn(USER_DN).build();
    assertEquals(2, ldapIdentity.getServer(), "unexpected server");
  }

  @Test
  void testSetInvalidServer() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          LdapIdentity.builder().server(0);
        });
  }

  @Test
  void testSetNullDn() {
    assertThrows(
        NullPointerException.class,
        () -> {
          LdapIdentity.builder().dn(null);
        });
  }

  @Test
  void testSetBlankdDn() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          LdapIdentity.builder().dn(" ");
        });
  }

  @Test
  void testMissingServer() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          LdapIdentity.builder().dn(USER_DN).build();
        });
  }

  @Test
  void testMissingDn() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          LdapIdentity.builder().server(1).build();
        });
  }

  @Test
  void testSetDn() {
    LdapIdentity ldapIdentity = LdapIdentity.builder().server(2).dn(USER_DN).build();
    assertEquals(USER_DN, ldapIdentity.getDn(), "unexpected server");
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testToBuilder() {

    LdapIdentity ldapIdentity = LdapIdentity.builder().server(2).dn(USER_DN).build();

    LdapIdentity changedLdapIdentity = ldapIdentity.toBuilder().build();

    assertEquals(2, changedLdapIdentity.getServer(), "unexpected server");
    assertEquals(USER_DN, changedLdapIdentity.getDn(), "dn");
  }

  @Test
  void testSerialize() {

    JsonMapper mapper =
        JsonMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .changeDefaultPropertyInclusion(
                inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
            .build();

    LdapIdentity ldapIdentity = LdapIdentity.builder().server(2).dn(USER_DN).build();

    String json = mapper.writeValueAsString(ldapIdentity);

    String expected = "{\"@type\":\"ldap\",\"server\":2,\"dn\":\"userdn\"}";

    assertEquals(expected, json, "unexpected json");
  }
}
