package com.sitepark.ies.userrepository.core.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.exception.AnchorNotFoundException;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class IdentifierResolverTest {

	@Test
	void testResolveWithId() {

		Identifier identifier = Identifier.ofId(123L);
		UserRepository repository = mock();
		IdentifierResolver resolver = new IdentifierResolver(repository);

		long id = resolver.resolveIdentifier(identifier);

		assertEquals(123L, id, "unexpected id");
	}

	@Test
	void testResolveWithAnchor() {

		Anchor anchor = Anchor.ofString("abc");
		Identifier identifier = Identifier.ofAnchor(anchor);
		UserRepository repository = mock();
		when(repository.resolveAnchor(any())).thenReturn(Optional.of(123L));
		IdentifierResolver resolver = new IdentifierResolver(repository);

		long id = resolver.resolveIdentifier(identifier);

		assertEquals(123L, id, "unexpected id");
	}

	@Test
	void testResolveWithAnchorNotFound() {

		Anchor anchor = Anchor.ofString("abc");
		Identifier identifier = Identifier.ofAnchor(anchor);
		UserRepository repository = mock();
		when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
		IdentifierResolver resolver = new IdentifierResolver(repository);

		assertThrows(AnchorNotFoundException.class, () -> {
			resolver.resolveIdentifier(identifier);
		});
	}
}
