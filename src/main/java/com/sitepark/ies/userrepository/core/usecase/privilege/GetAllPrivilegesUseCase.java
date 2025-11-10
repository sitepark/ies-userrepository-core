package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllPrivilegesUseCase {

  private final PrivilegeRepository repository;

  private final AccessControl accessControl;

  @Inject
  GetAllPrivilegesUseCase(PrivilegeRepository repository, AccessControl accessControl) {
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
