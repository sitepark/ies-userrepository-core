package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class IdentifierTest {

	@Test
	void testOfStringToId() {
		Identifier identifier = Identifier.ofString("123");
		assertEquals(Optional.of(123L), identifier.getId(), "id exprected");
	}

	@Test
	void testOfStringToAnchor() {
		Identifier identifier = Identifier.ofString("abc");
		Anchor anchor = Anchor.ofString("abc");
		assertEquals(Optional.of(anchor), identifier.getAnchor(), "anchor exprected");
	}

	@Test
	void testOfStringWithLongString() {
		Identifier identifier = Identifier.ofString("abcdefghijklmnopqrstuvwxyz");
		Anchor anchor = Anchor.ofString("abcdefghijklmnopqrstuvwxyz");
		assertEquals(Optional.of(anchor), identifier.getAnchor(), "anchor exprected");
	}

	@Test
	void testOfStringWithDot() {
		Identifier identifier = Identifier.ofString("123.b");
		Anchor anchor = Anchor.ofString("123.b");
		assertEquals(Optional.of(anchor), identifier.getAnchor(), "anchor exprected");
	}

	@Test
	void testOfId() {
		Identifier identifier = Identifier.ofId(123L);
		assertEquals(Optional.of(123L), identifier.getId(), "id exprected");
	}

	@Test
	void testOfAnchor() {
		Anchor anchor = Anchor.ofString("abc");
		Identifier identifier = Identifier.ofAnchor(anchor);
		assertEquals(
				Optional.of(Anchor.ofString("abc")),
				identifier.getAnchor(),
				"anchor exprected");
	}

	@Test
	void testOfAnchorWithNull() {
		assertThrows(NullPointerException.class, () -> {
			Identifier.ofAnchor(null);
		});
	}
}
