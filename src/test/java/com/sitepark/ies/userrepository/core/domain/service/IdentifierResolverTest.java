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

    String id = IdentifierResolver.create(repository).resolve(identifier);

    assertEquals("123", id, "unexpected id");
  }

  @Test
  void testResolveWithAnchor() {

    Anchor anchor = Anchor.ofString("abc");
    Identifier identifier = Identifier.ofAnchor(anchor);
    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.of("123"));

    String id = IdentifierResolver.create(repository).resolve(identifier);

    assertEquals("123", id, "unexpected id");
  }

  @Test
  void testResolveWithAnchorNotFound() {

    Anchor anchor = Anchor.ofString("abc");
    Identifier identifier = Identifier.ofAnchor(anchor);
    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());

    assertThrows(
        AnchorNotFoundException.class,
        () -> IdentifierResolver.create(repository).resolve(identifier));
  }

  @Test
  void testResolveListWithIds() {

    Identifier id1 = Identifier.ofId("123");
    Identifier id2 = Identifier.ofId("456");
    UserRepository repository = mock();

    var ids = IdentifierResolver.create(repository).resolve(java.util.List.of(id1, id2));

    assertEquals(java.util.List.of("123", "456"), ids, "unexpected ids");
  }

  @Test
  void testResolveListWithAnchors() {

    Anchor anchor1 = Anchor.ofString("abc");
    Anchor anchor2 = Anchor.ofString("def");
    Identifier id1 = Identifier.ofAnchor(anchor1);
    Identifier id2 = Identifier.ofAnchor(anchor2);
    UserRepository repository = mock();
    when(repository.resolveAnchor(anchor1)).thenReturn(Optional.of("123"));
    when(repository.resolveAnchor(anchor2)).thenReturn(Optional.of("456"));

    var ids = IdentifierResolver.create(repository).resolve(java.util.List.of(id1, id2));

    assertEquals(java.util.List.of("123", "456"), ids, "unexpected ids");
  }

  @Test
  void testResolveListWithAnchorNotFound() {

    Anchor anchor = Anchor.ofString("abc");
    Identifier identifier = Identifier.ofAnchor(anchor);
    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());

    assertThrows(
        AnchorNotFoundException.class,
        () -> IdentifierResolver.create(repository).resolve(java.util.List.of(identifier)));
  }
}
