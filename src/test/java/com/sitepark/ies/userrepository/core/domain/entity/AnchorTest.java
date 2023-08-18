package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAnchor;

import nl.jqno.equalsverifier.EqualsVerifier;

class AnchorTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(Anchor.class)
			.verify();
	}

	@Test
	void testOfNullString() {
		Anchor anchor = Anchor.ofString(null);
		assertNull(anchor, "anchor should be null");
	}

	@Test
	void testOfBlankString() {
		Anchor anchor = Anchor.ofString("  ");
		assertEquals(Anchor.EMPTY, anchor, "anchor should be Anchor.EMPTY");
	}

	@Test
	void testValidateValidAnchor() {
		assertDoesNotThrow(() -> {
			Anchor.ofString("123a");
		});
	}

	@Test
	void testValidateOnlyDigits() {
		InvalidAnchor thrown = assertThrows(InvalidAnchor.class, () -> {
			Anchor.ofString("1234556789012345");
		});
		assertEquals("1234556789012345", thrown.getName(), "unexpected name");
	}

	@Test
	void testValidateInvalidChars() {
		InvalidAnchor thrown = assertThrows(InvalidAnchor.class, () -> {
			Anchor.ofString("a.b,c");
		});
		assertEquals("a.b,c", thrown.getName(), "unexpected name");
	}

	@Test
	void testSerialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();

		Anchor anchor = Anchor.ofString("abc");

		String json = mapper.writeValueAsString(anchor);

		assertEquals("\"abc\"", json, "unexpected value");
	}

	@Test
	void testDeserialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();

		String json = "\"abc\"";

		Anchor anchor = mapper.readValue(json, Anchor.class);

		assertEquals("abc", anchor.getName(), "unexpected anchor");
	}
}
