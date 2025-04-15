package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllPrivileges {

  private final PrivilegeRepository repository;

  private final AccessControl accessControl;

  @Inject
  GetAllPrivileges(PrivilegeRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public List<Privilege> getAllPrivileges() {

    if (!this.accessControl.isPrivilegeReadable()) {
      throw new AccessDeniedException("Not allowed to read privileges");
    }

    return this.repository.getAll();
  }
}
