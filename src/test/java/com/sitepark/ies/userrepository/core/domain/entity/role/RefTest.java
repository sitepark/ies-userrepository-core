package com.sitepark.ies.userrepository.core.domain.entity.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;

import nl.jqno.equalsverifier.EqualsVerifier;

class RefTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(Ref.class)
			.withIgnoredFields("id", "anchor")
			.verify();
	}

	@Test
	@SuppressWarnings({
		"PMD.JUnitTestContainsTooManyAsserts",
		"PMD.AvoidDuplicateLiterals"
	})
	void testAnchorRef() {
		Ref ref = Ref.ofAnchor(Anchor.ofString("role.a"));
		assertEquals("REF(role.a)", ref.getName(), "unexpected name");
		assertEquals(Optional.empty(), ref.getId(), "empty id expected");
		assertEquals(Optional.of(Anchor.ofString("role.a")), ref.getAnchor(), "unexpected anchor");
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	void testAnchorStringRef() {
		Ref ref = Ref.ofAnchor("role.a");
		assertEquals("REF(role.a)", ref.getName(), "unexpected name");
		assertEquals(Optional.empty(), ref.getId(), "empty id expected");
		assertEquals(Optional.of(Anchor.ofString("role.a")), ref.getAnchor(), "unexpected anchor");
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	void testNullAnchorRef() {
		assertThrows(NullPointerException.class, () -> {
			Ref.ofAnchor((Anchor)null);
		});
	}

	@Test
	void testNullAnchorStringRef() {
		assertThrows(NullPointerException.class, () -> {
			Ref.ofAnchor((String)null);
		});
	}

	@Test
	void testNullIdRef() {
		assertThrows(IllegalArgumentException.class, () -> {
			Ref.ofId(0);
		});
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	void testIdRef() {
		Ref ref = Ref.ofId(123L);
		assertEquals("REF(123)", ref.getName(), "unexpected name");
		assertEquals(Optional.empty(), ref.getAnchor(), "empty anchor expected");
		assertEquals(Optional.of(123L), ref.getId(), "unexpected id");
	}

	@Test
	void testWorkarroundConstructor() {
		Ref ref = new Ref();
		assertEquals("NONE", ref.getName(), "unexpected role name");
	}
}
