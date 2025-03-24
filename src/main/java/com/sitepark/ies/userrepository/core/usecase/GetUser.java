package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;

public final class GetUser {

  private final UserRepository repository;

  private final IdentifierResolver identifierResolver;

  private final AccessControl accessControl;

  @Inject
  GetUser(
      UserRepository repository,
      IdentifierResolver identifierResolver,
      AccessControl accessControl) {
    this.repository = repository;
    this.identifierResolver = identifierResolver;
    this.accessControl = accessControl;
  }

  public User getUser(Identifier identifier) {

    String id = this.identifierResolver.resolveIdentifier(identifier);

    if (!this.accessControl.isUserReadable()) {
      throw new AccessDeniedException("Not allowed to read user");
    }

    return this.repository.get(id).orElseThrow(() -> new UserNotFoundException(id));
  }
}
