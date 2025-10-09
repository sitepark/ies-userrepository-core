package com.sitepark.ies.userrepository.core.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class IdentifierResolverTest {

  @Test
  void testResolveWithId() {

    Identifier identifier = Identifier.ofId("123");
    UserRepository repository = mock();
    IdentifierResolver resolver = new IdentifierResolver(repository);

    String id = resolver.resolve(identifier);

    assertEquals("123", id, "unexpected id");
  }

  @Test
  void testResolveWithAnchor() {

    Anchor anchor = Anchor.ofString("abc");
    Identifier identifier = Identifier.ofAnchor(anchor);
    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.of("123"));
    IdentifierResolver resolver = new IdentifierResolver(repository);

    String id = resolver.resolve(identifier);

    assertEquals("123", id, "unexpected id");
  }

  @Test
  void testResolveWithAnchorNotFound() {

    Anchor anchor = Anchor.ofString("abc");
    Identifier identifier = Identifier.ofAnchor(anchor);
    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
    IdentifierResolver resolver = new IdentifierResolver(repository);

    assertThrows(AnchorNotFoundException.class, () -> resolver.resolve(identifier));
  }
}
