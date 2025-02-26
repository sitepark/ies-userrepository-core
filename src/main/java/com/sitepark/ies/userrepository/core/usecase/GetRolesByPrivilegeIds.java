package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetRolesByPrivilegeIds {

  private final RoleRepository repository;

  private final AccessControl accessControl;

  @Inject
  protected GetRolesByPrivilegeIds(RoleRepository repository, AccessControl accessControl) {
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
