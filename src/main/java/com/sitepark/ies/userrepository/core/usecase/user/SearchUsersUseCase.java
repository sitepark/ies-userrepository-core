package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import jakarta.inject.Inject;

public final class SearchUsersUseCase {

  private final UserRepository repository;

  private final UserEntityAuthorizationService userEntityAuthorizationService;

  @Inject
  SearchUsersUseCase(
      UserRepository repository, UserEntityAuthorizationService userEntityAuthorizationService) {
    this.repository = repository;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
  }

  public Result<User> searchUsers(Query query) {

    Result<User> users = this.repository.search(query);

    if (!this.userEntityAuthorizationService.isReadable(
        users.items().stream().map(User::id).toList())) {
      throw new AccessDeniedException("Not allowed to read users");
    }

    return users;
  }
}
