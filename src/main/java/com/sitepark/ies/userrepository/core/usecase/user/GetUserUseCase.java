package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;

public final class GetUserUseCase {

  private final UserRepository repository;

  private final UserEntityAuthorizationService userEntityAuthorizationService;

  @Inject
  GetUserUseCase(
      UserRepository repository, UserEntityAuthorizationService userEntityAuthorizationService) {
    this.repository = repository;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
  }

  public User getUser(Identifier identifier) {

    String id = IdentifierResolver.create(this.repository).resolve(identifier);

    if (!this.userEntityAuthorizationService.isReadable(id)) {
      throw new AccessDeniedException("Not allowed to read user");
    }

    return this.repository.get(id).orElseThrow(() -> new UserNotFoundException(id));
  }
}
