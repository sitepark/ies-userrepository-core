package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllUsers {

  private final UserRepository repository;

  private final AccessControl accessControl;

  @Inject
  GetAllUsers(UserRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public List<User> getAllUsers(Filter filter) {

    if (!this.accessControl.isUserReadable()) {
      throw new AccessDeniedException("Not allowed to read users");
    }

    return this.repository.getAll(filter);
  }
}
