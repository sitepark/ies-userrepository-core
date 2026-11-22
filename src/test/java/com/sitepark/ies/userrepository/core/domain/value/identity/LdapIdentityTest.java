package com.sitepark.ies.userrepository.core.domain.value.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sitepark.ies.userrepository.core.domain.value.Identity;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

class LdapIdentityTest {

  private static final String USER_DN = "userdn";

  @Test
  void testEquals() {
    EqualsVerifier.forClass(LdapIdentity.class).verify();
  }

  @Test
  void testSetServer() {
    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();
    assertEquals("2", ldapIdentity.serverId(), "unexpected server");
  }

  @Test
  void testSetInvalidServer() {
    assertThrows(NullPointerException.class, () -> LdapIdentity.builder().serverId(null));
  }

  @Test
  void testSetNullDn() {
    assertThrows(NullPointerException.class, () -> LdapIdentity.builder().dn(null));
  }

  @Test
  void testSetBlankDn() {
    assertThrows(IllegalArgumentException.class, () -> LdapIdentity.builder().dn(" "));
  }

  @Test
  void testMissingServer() {
    assertThrows(IllegalStateException.class, () -> LdapIdentity.builder().dn(USER_DN).build());
  }

  @Test
  void testMissingDn() {
    assertThrows(IllegalStateException.class, () -> LdapIdentity.builder().serverId("1").build());
  }

  @Test
  void testSetDn() {
    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();
    assertEquals(USER_DN, ldapIdentity.dn(), "unexpected server");
  }

  @Test
  void testToBuilder() {

    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    LdapIdentity copy = ldapIdentity.toBuilder().serverId("3").build();

    LdapIdentity expected = LdapIdentity.builder().serverId("3").dn(USER_DN).build();

    assertEquals(expected, copy, "unexpected ldapIdentity");
  }

  @Test
  void testSerialize() {

    JsonMapper mapper =
        JsonMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .changeDefaultPropertyInclusion(
                inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
            .build();

    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    String json = mapper.writeValueAsString(ldapIdentity);

    String expected = "{\"serverId\":\"2\",\"dn\":\"userdn\",\"type\":\"ldap\"}";

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testSerializeInList() {

    JsonMapper mapper =
        JsonMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .changeDefaultPropertyInclusion(
                inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
            .build();

    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    List<Identity> identities = List.of(ldapIdentity);
    String json =
        mapper.writerFor(new TypeReference<List<Identity>>() {}).writeValueAsString(identities);

    String expected = "[{\"serverId\":\"2\",\"dn\":\"userdn\",\"type\":\"ldap\"}]";

    assertEquals(expected, json, "unexpected json");
  }
}
