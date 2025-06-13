package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;

public class IdentifierResolver {

  private final UserRepository repository;

  @Inject
  protected IdentifierResolver(UserRepository repository) {
    this.repository = repository;
  }

  public String resolveIdentifier(Identifier identifier) {

    if (identifier.getId() != null) {
      return identifier.getId();
    }

    assert identifier.getAnchor() != null;

    return this.repository
        .resolveAnchor(identifier.getAnchor())
        .orElseThrow(() -> new AnchorNotFoundException(identifier.getAnchor()));
  }
}
