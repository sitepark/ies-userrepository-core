package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.query.Query;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
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

  public List<User> getAllUsers(Query query) {

    if (!this.accessControl.isUserReadable()) {
      throw new AccessDeniedException("Not allowed to read users");
    }

    return this.repository.getAll(query);
  }
}
