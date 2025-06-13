package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL",
  "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"
})
class AccessTokenTest {

  private static final ZoneId ZONE_ID = ZoneId.of("Europe/Berlin");

  private static final String TOKEN_NAME = "Test Token";

  @Test
  void testEquals() {
    EqualsVerifier.forClass(AccessToken.class).verify();
  }

  @Test
  void testSetUser() {
    AccessToken accessToken = AccessToken.builder().user("345").name(TOKEN_NAME).build();
    assertEquals("345", accessToken.getUser(), "wrong user");
  }

  @Test
  void testSetUserWithNull() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().user(null));
  }

  @Test
  void testSetUserWithZero() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().user("0"));
  }

  @Test
  void testSetUserWithInvalidValue() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().user("1x"));
  }

  @Test
  void testSetName() {
    AccessToken accessToken = AccessToken.builder().user("345").name(TOKEN_NAME).build();
    assertEquals(TOKEN_NAME, accessToken.getName(), "wrong name");
  }

  @Test
  void testSetNullName() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().name(null));
  }

  @Test
  void testSetBlankName() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().name(" "));
  }

  @Test
  void testBuildUserNotSet() {
    assertThrows(IllegalStateException.class, () -> AccessToken.builder().name(TOKEN_NAME).build());
  }

  @Test
  void testBuildNameNotSet() {
    assertThrows(IllegalStateException.class, () -> AccessToken.builder().user("123").build());
  }

  @Test
  void testSetId() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().id("123").build();
    assertEquals("123", accessToken.getId(), "wrong id");
  }

  @Test
  void testGetEmptyId() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().build();
    assertNull(accessToken.getId(), "id should be null");
  }

  @Test
  void testSetIdWithNull() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().id(null));
  }

  @Test
  void testSetIdWithZero() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().id("0"));
  }

  @Test
  void testSetIdWithInvalidValue() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().id("1x"));
  }

  @Test
  void testSetToken() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().token("abc").build();
    assertEquals("abc", accessToken.getToken(), "wrong token");
  }

  @Test
  void testSetNullToken() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().token(null));
  }

  @Test
  void testSetBlankToken() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().token(" "));
  }

  @Test
  void testSetCreatedAt() {

    OffsetDateTime createdAt =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    AccessToken accessToken = this.createBuilderWithRequiredValues().createdAt(createdAt).build();

    assertEquals(createdAt, accessToken.getCreatedAt(), "unexpected createAt");
  }

  @Test
  void testSetNullCreatedAt() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().createdAt(null));
  }

  @Test
  void testSetExpiresAt() {

    OffsetDateTime expiresAt =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    AccessToken accessToken = this.createBuilderWithRequiredValues().expiresAt(expiresAt).build();

    assertEquals(expiresAt, accessToken.getExpiresAt(), "unexpected expiresAt");
  }

  @Test
  void testSetNullExpiresAt() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().expiresAt(null));
  }

  @Test
  void testSetLastUsed() {

    OffsetDateTime lastUsed =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    AccessToken accessToken = this.createBuilderWithRequiredValues().lastUsed(lastUsed).build();

    assertEquals(lastUsed, accessToken.getLastUsed(), "unexpected lastUsed");
  }

  @Test
  void testSetNullLastUsed() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().lastUsed(null));
  }

  @Test
  void testSetScopeListViaList() {
    AccessToken accessToken =
        this.createBuilderWithRequiredValues().scopeList(Arrays.asList("a", "b")).build();

    assertEquals(Arrays.asList("a", "b"), accessToken.getScopeList(), "unexpected scopeList");
  }

  @Test
  void testSetNullScopeListViaList() {
    assertThrows(
        NullPointerException.class, () -> AccessToken.builder().scopeList((List<String>) null));
  }

  @Test
  void testSetNullScopeViaList() {
    assertThrows(
        NullPointerException.class,
        () -> AccessToken.builder().scopeList(Arrays.asList("a", null)));
  }

  @Test
  void testSetBlankScopeListViaList() {
    assertThrows(
        IllegalArgumentException.class,
        () -> AccessToken.builder().scopeList(Arrays.asList("a", " ")));
  }

  @Test
  void testOverwriteScopeListViaList() {
    AccessToken accessToken =
        this.createBuilderWithRequiredValues().scopeList(Arrays.asList("a", "b")).build();

    AccessToken overwritten = accessToken.toBuilder().scopeList(Arrays.asList("c", "d")).build();

    assertEquals(Arrays.asList("c", "d"), overwritten.getScopeList(), "unexpected scopeList");
  }

  @Test
  void testSetScopeListViaVArgs() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().scopeList("a", "b").build();

    assertEquals(Arrays.asList("a", "b"), accessToken.getScopeList(), "unexpected scopeList");
  }

  @Test
  void testOverwriteScopeListViaVArgs() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().scopeList("a", "b").build();
    AccessToken overwritten = accessToken.toBuilder().scopeList("c", "d").build();

    assertEquals(Arrays.asList("c", "d"), overwritten.getScopeList(), "unexpected scopeList");
  }

  @Test
  void testSetNullScopeListViaVArgs() {
    assertThrows(
        NullPointerException.class, () -> AccessToken.builder().scopeList((String[]) null));
  }

  @Test
  void testSetNullScopeViaVArgs() {
    assertThrows(NullPointerException.class, () -> AccessToken.builder().scopeList("a", null));
  }

  @Test
  void testSetBlankScopeViaVArgs() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().scopeList("a", " "));
  }

  @Test
  void testAddScope() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().scopeList("a", "b").build();

    AccessToken addition = accessToken.toBuilder().scope("c").build();

    assertEquals(Arrays.asList("a", "b", "c"), addition.getScopeList(), "unexpected scopes");
  }

  @Test
  void testAddNullScope() {
    assertThrows(
        NullPointerException.class, () -> this.createBuilderWithRequiredValues().scope(null));
  }

  @Test
  void testAddBlankScope() {
    assertThrows(IllegalArgumentException.class, () -> AccessToken.builder().scope(" "));
  }

  @Test
  void testSetImpersonationTrue() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().impersonation(true).build();
    assertTrue(accessToken.isImpersonation(), "unexpected impersonation");
  }

  @Test
  void testSetImpersonationFalse() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().impersonation(false).build();
    assertFalse(accessToken.isImpersonation(), "unexpected impersonation");
  }

  @Test
  void testSetActiveTrue() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().active(true).build();
    assertTrue(accessToken.isActive(), "unexpected active");
  }

  @Test
  void testSetActiveFalse() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().active(false).build();
    assertFalse(accessToken.isActive(), "unexpected active");
  }

  @Test
  void testSetRevokedTrue() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().revoked(true).build();
    assertTrue(accessToken.isRevoked(), "unexpected revoked");
  }

  @Test
  void testSetRevokedFalse() {
    AccessToken accessToken = this.createBuilderWithRequiredValues().revoked(false).build();
    assertFalse(accessToken.isRevoked(), "unexpected revoked");
  }

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    OffsetDateTime createdAt =
        LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
    OffsetDateTime expiredAt =
        LocalDate.of(2023, 12, 12).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
    OffsetDateTime lastUpdate =
        LocalDate.of(2023, 8, 25).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

    AccessToken accessToken =
        AccessToken.builder()
            .id("123")
            .user("345")
            .name(TOKEN_NAME)
            .createdAt(createdAt)
            .expiresAt(expiredAt)
            .lastUsed(lastUpdate)
            .impersonation(true)
            .build();

    String json = mapper.writeValueAsString(accessToken);

    String expected =
        """
        {\
        "id":"123",\
        "user":"345",\
        "name":"Test Token",\
        "createdAt":"2023-08-21T00:00:00+02:00",\
        "expiresAt":"2023-12-12T00:00:00+01:00",\
        "lastUsed":"2023-08-25T00:00:00+02:00",\
        "impersonation":true,\
        "active":true,\
        "revoked":false\
        }\
        """;

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String json =
        """
        {\
        "id":123,\
        "user":345,\
        "name":"Test Token",\
        "createdAt":"2023-08-21T00:00:00+02:00",\
        "expiresAt":"2023-12-12T00:00:00+01:00",\
        "lastUsed":"2023-08-25T00:00:00+02:00",\
        "impersonation":true,\
        "active":true,\
        "revoked":false\
        }\
        """;

    AccessToken accessToken = mapper.readValue(json, AccessToken.class);

    OffsetDateTime createdAt =
        LocalDate.of(2023, 8, 21)
            .atStartOfDay()
            .atZone(ZONE_ID)
            .toOffsetDateTime()
            .withOffsetSameInstant(ZoneOffset.UTC);
    OffsetDateTime expiredAt =
        LocalDate.of(2023, 12, 12)
            .atStartOfDay()
            .atZone(ZONE_ID)
            .toOffsetDateTime()
            .withOffsetSameInstant(ZoneOffset.UTC);
    OffsetDateTime lastUpdate =
        LocalDate.of(2023, 8, 25)
            .atStartOfDay()
            .atZone(ZONE_ID)
            .toOffsetDateTime()
            .withOffsetSameInstant(ZoneOffset.UTC);

    AccessToken expected =
        AccessToken.builder()
            .id("123")
            .user("345")
            .name(TOKEN_NAME)
            .createdAt(createdAt)
            .expiresAt(expiredAt)
            .lastUsed(lastUpdate)
            .impersonation(true)
            .build();

    assertEquals(expected, accessToken, "unexpected accessToken");
  }

  private AccessToken.Builder createBuilderWithRequiredValues() {
    return AccessToken.builder().user("345").name(TOKEN_NAME);
  }
}
