package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;

public final class GetUserUseCase {

  private final UserRepository repository;

  private final AccessControl accessControl;

  @Inject
  GetUserUseCase(UserRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public User getUser(Identifier identifier) {

    String id = IdentifierResolver.create(this.repository).resolve(identifier);

    if (!this.accessControl.isUserReadable()) {
      throw new AccessDeniedException("Not allowed to read user");
    }

    return this.repository.get(id).orElseThrow(() -> new UserNotFoundException(id));
  }
}
