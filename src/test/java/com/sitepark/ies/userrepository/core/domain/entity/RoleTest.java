package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL"
})
class RoleTest {

  private static final String TEST_ROLE_NAME = "ADMINISTRATOR";

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Role.class).verify();
  }

  @Test
  void testOfName() {
    Role role = Role.ofName(TEST_ROLE_NAME);
    assertEquals(TEST_ROLE_NAME, role.getName(), "unexprected name");
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testOfNameWithNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Role.ofName(null);
        });
  }

  @Test
  void testOfNameWithBlank() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          Role.ofName(" ");
        });
  }

  @Test
  void testToString() {
    Role role = Role.ofName(TEST_ROLE_NAME);
    assertEquals(TEST_ROLE_NAME, role.toString(), "unexprected string representation");
  }

  @Test
  void testSerialize() {

    JsonMapper mapper =
        JsonMapper.builder().disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY).build();

    Role role = Role.ofName(TEST_ROLE_NAME);

    String json = mapper.writeValueAsString(role);

    assertEquals("\"" + TEST_ROLE_NAME + "\"", json, "unexpected value");
  }

  @Test
  void testDeserialize() {

    JsonMapper mapper =
        JsonMapper.builder().disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY).build();

    String json = "\"" + TEST_ROLE_NAME + "\"";

    Role role = mapper.readValue(json, Role.class);

    assertEquals(TEST_ROLE_NAME, role.getName(), "unexpected anchor");
  }
}
