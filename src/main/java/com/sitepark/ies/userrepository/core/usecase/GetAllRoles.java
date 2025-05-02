package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllRoles {

  private final RoleRepository repository;

  private final AccessControl accessControl;

  @Inject
  GetAllRoles(RoleRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public List<Role> getAllRoles() {

    if (!this.accessControl.isRoleReadable()) {
      throw new AccessDeniedException("Not allowed to read roles");
    }

    return this.repository.getAll();
  }
}
