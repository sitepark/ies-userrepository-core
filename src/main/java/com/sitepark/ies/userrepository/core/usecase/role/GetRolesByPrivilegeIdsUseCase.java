package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetRolesByPrivilegeIdsUseCase {

  private final RoleRepository repository;

  private final AccessControl accessControl;

  @Inject
  GetRolesByPrivilegeIdsUseCase(RoleRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public List<Role> getRolesByPrivilegeIds(List<String> privilegeIds) {

    if (!this.accessControl.isRoleReadable()) {
      throw new AccessDeniedException("Not allowed to read roles");
    }

    return this.repository.getByPrivilegeIds(privilegeIds);
  }
}
