package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.jqno.equalsverifier.EqualsVerifier;

@SuppressWarnings("PMD.TooManyMethods")
class AccessTokenTest {

	private static final ZoneId ZONE_ID = ZoneId.of( "Europe/Berlin" );

	private static final String TOKEN_NAME = "Test Token";

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(AccessToken.class)
			.verify();
	}

	@Test
	void testSetUser() throws JsonProcessingException {
		AccessToken accessToken = AccessToken.builder()
				.user("345")
				.name(TOKEN_NAME)
				.build();
		assertEquals("345", accessToken.getUser(), "wrong user");
	}

	@Test
	void testSetInvalidUser() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().user("0");
		});
	}

	@Test
	void testSetName() throws JsonProcessingException {
		AccessToken accessToken = AccessToken.builder()
				.user("345")
				.name(TOKEN_NAME)
				.build();
		assertEquals(TOKEN_NAME, accessToken.getName(), "wrong name");
	}

	@Test
	void testSetNullName() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().name(null);
		});
	}

	@Test
	void testSetBlankName() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().name(" ");
		});
	}

	@Test
	void testBuildUserNotSet() throws JsonProcessingException {
		assertThrows(IllegalStateException.class, () -> {
			AccessToken.builder()
				.name(TOKEN_NAME)
				.build();
		});
	}

	@Test
	void testBuildNameNotSet() throws JsonProcessingException {
		assertThrows(IllegalStateException.class, () -> {
			AccessToken.builder()
				.user("123")
				.build();
		});
	}

	@Test
	void testSetId() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.id("123")
				.build();
		assertEquals("123", accessToken.getId().get(), "wrong id");
	}

	@Test
	void testGetEmptyId() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.build();
		assertTrue(accessToken.getId().isEmpty(), "id should be empty");
	}

	@Test
	void testSetInvalidId() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().id("0");
		});
	}

	@Test
	void testSetToken() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.token("abc")
				.build();
		assertEquals("abc", accessToken.getToken().get(), "wrong token");
	}

	@Test
	void testSetNullToken() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().token(null);
		});
	}

	@Test
	void testSetBlankToken() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().token(" ");
		});
	}

	@Test
	void testSetCreatedAt() throws JsonProcessingException {

		OffsetDateTime createdAt = LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

		AccessToken accessToken = this.createBuilderWithRequiredValues()
					.createdAt(createdAt)
					.build();

		assertEquals(createdAt, accessToken.getCreatedAt().get(), "unexpected createAt");
	}

	@Test
	void testSetNullCreatedAt() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().createdAt(null);
		});
	}

	@Test
	void testSetExpiresAt() throws JsonProcessingException {

		OffsetDateTime expiresAt = LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

		AccessToken accessToken = this.createBuilderWithRequiredValues()
					.expiresAt(expiresAt)
					.build();

		assertEquals(expiresAt, accessToken.getExpiresAt().get(), "unexpected expiresAt");
	}

	@Test
	void testSetNullExpiresAt() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().expiresAt(null);
		});
	}

	@Test
	void testSetLastUsed() throws JsonProcessingException {

		OffsetDateTime lastUsed = LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

		AccessToken accessToken = this.createBuilderWithRequiredValues()
					.lastUsed(lastUsed)
					.build();

		assertEquals(lastUsed, accessToken.getLastUsed().get(), "unexpected lastUsed");
	}

	@Test
	void testSetNullLastUsed() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().lastUsed(null);
		});
	}

	@Test
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	void testSetScopeListViaList() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.scopeList(Arrays.asList("a", "b"))
				.build();

		assertEquals(Arrays.asList("a", "b"), accessToken.getScopeList(), "unexpected scopeList");
	}

	@Test
	void testSetNullScopeListViaList() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().scopeList((List<String>)null);
		});
	}

	@Test
	void testSetNullScopeViaList() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().scopeList(Arrays.asList("a", null));
		});
	}

	@Test
	void testSetBlankScopeListViaList() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().scopeList(Arrays.asList("a", " "));
		});
	}

	@Test
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	void testOverwriteScopeListViaList() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.scopeList(Arrays.asList("a", "b"))
				.build();

		AccessToken overwritten = accessToken.toBuilder()
				.scopeList(Arrays.asList("c", "d"))
				.build();

		assertEquals(Arrays.asList("c", "d"), overwritten.getScopeList(), "unexpected scopeList");
	}

	@Test
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	void testSetScopeListViaVArgs() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.scopeList("a", "b")
				.build();

		assertEquals(Arrays.asList("a", "b"), accessToken.getScopeList(), "unexpected scopeList");
	}

	@Test
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	void testOverwriteScopeListViaVArgs() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.scopeList("a", "b")
				.build();
		AccessToken overwritten = accessToken.toBuilder()
				.scopeList("c", "d")
				.build();

		assertEquals(Arrays.asList("c", "d"), overwritten.getScopeList(), "unexpected scopeList");
	}

	@Test
	void testSetNullScopeListViaVArgs() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().scopeList((String[])null);
		});
	}

	@Test
	void testSetNullScopeViaVArgs() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			AccessToken.builder().scopeList("a", null);
		});
	}

	@Test
	void testSetBlankScopeViaVArgs() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().scopeList("a", " ");
		});
	}

	@Test
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	void testAddScope() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.scopeList("a", "b")
				.build();

		AccessToken addition = accessToken.toBuilder()
				.scope("c")
				.build();

		assertEquals(Arrays.asList("a", "b", "c"), addition.getScopeList(), "unexpected scopes");
	}

	@Test
	void testAddNullScope() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			this.createBuilderWithRequiredValues()
					.scope(null);
		});
	}

	@Test
	void testAddBlankScope() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			AccessToken.builder().scope(" ");
		});
	}

	@Test
	void testSetImpersonationTrue() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.impersonation(true)
				.build();
		assertTrue(accessToken.isImpersonation(), "unexpected impersonation");
	}

	@Test
	void testSetImpersonationFalse() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.impersonation(false)
				.build();
		assertFalse(accessToken.isImpersonation(), "unexpected impersonation");
	}

	@Test
	void testSetActiveTrue() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.active(true)
				.build();
		assertTrue(accessToken.isActive(), "unexpected active");
	}

	@Test
	void testSetActiveFalse() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.active(false)
				.build();
		assertFalse(accessToken.isActive(), "unexpected active");
	}

	@Test
	void testSetRevokedTrue() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.revoked(true)
				.build();
		assertTrue(accessToken.isRevoked(), "unexpected revoked");
	}

	@Test
	void testSetRevokedFalse() throws JsonProcessingException {
		AccessToken accessToken = this.createBuilderWithRequiredValues()
				.revoked(false)
				.build();
		assertFalse(accessToken.isRevoked(), "unexpected revoked");
	}

	@Test
	void testSerialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

		OffsetDateTime createdAt = LocalDate.of(2023, 8, 21).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
		OffsetDateTime expiredAt = LocalDate.of(2023, 12, 12).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
		OffsetDateTime lastUpdate = LocalDate.of(2023, 8, 25).atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();

		AccessToken accessToken = AccessToken.builder()
				.id("123")
				.user("345")
				.name(TOKEN_NAME)
				.createdAt(createdAt)
				.expiresAt(expiredAt)
				.lastUsed(lastUpdate)
				.impersonation(true)
				.build();

		String json = mapper.writeValueAsString(accessToken);

		String expected = "{" +
				"\"id\":\"123\"," +
				"\"user\":\"345\"," +
				"\"name\":\"Test Token\"," +
				"\"createdAt\":\"2023-08-21T00:00:00+02:00\"," +
				"\"expiresAt\":\"2023-12-12T00:00:00+01:00\"," +
				"\"lastUsed\":\"2023-08-25T00:00:00+02:00\"," +
				"\"impersonation\":true," +
				"\"active\":true," +
				"\"revoked\":false" +
		"}";

		assertEquals(expected, json, "unexpected json");
	}

	@Test
	void testDeserialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		String json = "{" +
				"\"id\":123," +
				"\"user\":345," +
				"\"name\":\"Test Token\"," +
				"\"createdAt\":\"2023-08-21T00:00:00+02:00\"," +
				"\"expiresAt\":\"2023-12-12T00:00:00+01:00\"," +
				"\"lastUsed\":\"2023-08-25T00:00:00+02:00\"," +
				"\"impersonation\":true," +
				"\"active\":true," +
				"\"revoked\":false" +
		"}";

		AccessToken accessToken = mapper.readValue(json, AccessToken.class);

		OffsetDateTime createdAt = LocalDate.of(2023, 8, 21)
				.atStartOfDay()
				.atZone(ZONE_ID)
				.toOffsetDateTime()
				.withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime expiredAt = LocalDate.of(2023, 12, 12)
				.atStartOfDay()
				.atZone(ZONE_ID)
				.toOffsetDateTime()
				.withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime lastUpdate = LocalDate.of(2023, 8, 25)
				.atStartOfDay()
				.atZone(ZONE_ID)
				.toOffsetDateTime()
				.withOffsetSameInstant(ZoneOffset.UTC);

		AccessToken expected = AccessToken.builder()
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
		return AccessToken.builder()
				.user("345")
				.name(TOKEN_NAME);
	}

}
