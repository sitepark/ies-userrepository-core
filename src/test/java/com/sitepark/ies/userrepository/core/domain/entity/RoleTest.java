package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.jqno.equalsverifier.EqualsVerifier;

class RoleTest {

	private static final String TEST_ROLE_NAME = "ADMINISTRATOR";

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(Role.class)
			.verify();
	}

	@Test
	void testOfName() {
		Role role = Role.ofName(TEST_ROLE_NAME);
		assertEquals(TEST_ROLE_NAME, role.getName(), "unexprected name");
	}

	@Test
	void testOfNameWithNull() {
		assertThrows(AssertionError.class, () -> {
			Role.ofName(null);
		});
	}

	@Test
	void testOfNameWithBlank() {
		assertThrows(AssertionError.class, () -> {
			Role.ofName(" ");
		});
	}

	@Test
	void testToString() {
		Role role = Role.ofName(TEST_ROLE_NAME);
		assertEquals(TEST_ROLE_NAME, role.toString(), "unexprected string representation");
	}

	@Test
	void testSerialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();

		Role role = Role.ofName(TEST_ROLE_NAME);

		String json = mapper.writeValueAsString(role);

		assertEquals("\"" + TEST_ROLE_NAME + "\"", json, "unexpected value");
	}

	@Test
	void testDeserialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();

		String json = "\"" + TEST_ROLE_NAME + "\"";

		Role role = mapper.readValue(json, Role.class);

		assertEquals(TEST_ROLE_NAME, role.getName(), "unexpected anchor");
	}

}
