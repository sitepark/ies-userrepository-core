package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import jakarta.inject.Inject;

public final class SearchUsers {

  private final UserRepository repository;

  private final AccessControl accessControl;

  @Inject
  SearchUsers(UserRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public Result<User> searchUsers(Query query) {

    if (!this.accessControl.isUserReadable()) {
      throw new AccessDeniedException("Not allowed to read users");
    }

    return this.repository.search(query);
  }
}
