package com.sitepark.ies.userrepository.core.domain.entity.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RefFactoryTest {

	@Test
	void testAcceptWithId() {
		RefFactory factory = new RefFactory();
		assertTrue(factory.accept("REF(123)"), "should accepted");
	}

	@Test
	void testAcceptWithAnchor() {
		RefFactory factory = new RefFactory();
		assertTrue(factory.accept("REF(group.user)"), "should accepted");
	}

	@Test
	void testNotAccept() {
		RefFactory factory = new RefFactory();
		assertFalse(factory.accept("ADMINISTRATOR"), "should not accepted");
	}

	@Test
	void testCreateWithId() {
		RefFactory factory = new RefFactory();
		Ref ref = factory.create("REF(123)");
		assertEquals("123", ref.getId().get(), "unexpected id");
		assertTrue(ref.getAnchor().isEmpty(), "anchor should be empty");
	}

	@Test
	void testCreateWithAnchor() {
		RefFactory factory = new RefFactory();
		Ref ref = factory.create("REF(group.user)");
		assertEquals("group.user", ref.getAnchor().get().getName(), "unexpected anchor");
		assertTrue(ref.getId().isEmpty(), "id should be empty");
	}

	@Test
	void testCreateInvalid() {
		RefFactory factory = new RefFactory();
		assertThrows(IllegalArgumentException.class, () -> {
			factory.create("GROUP(a/g)");
		});
	}
}
