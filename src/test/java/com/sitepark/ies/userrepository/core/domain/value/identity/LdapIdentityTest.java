package com.sitepark.ies.userrepository.core.domain.value.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.sitepark.ies.userrepository.core.domain.value.Identity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL",
  "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"
})
class LdapIdentityTest {

  private static final String USER_DN = "userdn";

  @Test
  void testEquals() {
    EqualsVerifier.forClass(LdapIdentity.class).verify();
  }

  @Test
  void testSetServer() {
    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();
    assertEquals("2", ldapIdentity.getServerId(), "unexpected server");
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
    assertEquals(USER_DN, ldapIdentity.getDn(), "unexpected server");
  }

  @Test
  void testToBuilder() {

    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    LdapIdentity copy = ldapIdentity.toBuilder().serverId("3").build();

    LdapIdentity expected = LdapIdentity.builder().serverId("3").dn(USER_DN).build();

    assertEquals(expected, copy, "unexpected ldapIdentity");
  }

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    String json = mapper.writeValueAsString(ldapIdentity);

    String expected = "{\"@type\":\"ldap\",\"serverId\":\"2\",\"dn\":\"userdn\"}";

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testSerializeInList() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    LdapIdentity ldapIdentity = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    List<Identity> identities = List.of(ldapIdentity);
    String json =
        mapper.writerFor(new TypeReference<List<Identity>>() {}).writeValueAsString(identities);

    String expected = "[{\"@type\":\"ldap\",\"serverId\":\"2\",\"dn\":\"userdn\"}]";

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    SimpleModule module = new SimpleModule();
    mapper.registerModule(module);

    String json = "{\"@type\":\"ldap\",\"serverId\":\"2\",\"dn\":\"userdn\"}";

    LdapIdentity ldapIdentity = mapper.readValue(json, LdapIdentity.class);

    LdapIdentity expected = LdapIdentity.builder().serverId("2").dn(USER_DN).build();

    assertEquals(expected, ldapIdentity, "unexpected ldapIdentity");
  }
}
