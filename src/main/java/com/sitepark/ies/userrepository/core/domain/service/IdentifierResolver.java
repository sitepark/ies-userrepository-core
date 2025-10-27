package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.port.AnchorResolver;
import java.util.List;

public final class IdentifierResolver {

  private final AnchorResolver anchorResolver;

  private IdentifierResolver(AnchorResolver anchorResolver) {
    this.anchorResolver = anchorResolver;
  }

  public static IdentifierResolver create(AnchorResolver anchorResolver) {
    return new IdentifierResolver(anchorResolver);
  }

  public List<String> resolve(List<Identifier> identifiers) {

    return identifiers.stream()
        .map(
            identifier ->
                identifier.resolveId(
                    (anchor) ->
                        this.anchorResolver
                            .resolveAnchor(identifier.getAnchor())
                            .orElseThrow(() -> new AnchorNotFoundException(anchor))))
        .toList();
  }

  public String resolve(Identifier identifier) {
    return identifier.resolveId(
        (anchor) ->
            this.anchorResolver
                .resolveAnchor(identifier.getAnchor())
                .orElseThrow(() -> new AnchorNotFoundException(anchor)));
  }
}
