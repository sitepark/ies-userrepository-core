package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllUsersUseCase {

  private final UserRepository repository;

  private final UserEntityAuthorizationService userEntityAuthorizationService;

  @Inject
  GetAllUsersUseCase(
      UserRepository repository, UserEntityAuthorizationService userEntityAuthorizationService) {
    this.repository = repository;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
  }

  public List<User> getAllUsers(Filter filter) {

    List<User> users = this.repository.getAll(filter);

    if (!this.userEntityAuthorizationService.isReadable(users.stream().map(User::id).toList())) {
      throw new AccessDeniedException("Not allowed to read users");
    }

    return users;
  }
}
