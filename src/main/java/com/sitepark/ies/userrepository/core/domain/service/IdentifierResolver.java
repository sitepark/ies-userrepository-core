package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.anchor.exception.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.Optional;

public class IdentifierResolver {

  private final UserRepository repository;

  @Inject
  protected IdentifierResolver(UserRepository repository) {
    this.repository = repository;
  }

  public String resolveIdentifier(Identifier identifier) {

    if (identifier.getId().isPresent()) {
      return identifier.getId().get();
    }

    assert identifier.getAnchor().isPresent();

    Optional<String> id = this.repository.resolveAnchor(identifier.getAnchor().get());
    if (id.isEmpty()) {
      throw new AnchorNotFoundException(identifier.getAnchor().get());
    }
    return id.get();
  }
}
