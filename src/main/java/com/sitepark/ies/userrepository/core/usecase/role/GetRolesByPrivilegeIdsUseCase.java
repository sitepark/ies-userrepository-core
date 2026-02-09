package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetRolesByPrivilegeIdsUseCase {

  private final RoleRepository repository;

  private final RoleEntityAuthorizationService roleEntityAuthorizationService;

  @Inject
  GetRolesByPrivilegeIdsUseCase(
      RoleRepository repository, RoleEntityAuthorizationService roleEntityAuthorizationService) {
    this.repository = repository;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
  }

  public List<Role> getRolesByPrivilegeIds(List<String> privilegeIds) {

    List<Role> roles = this.repository.getByPrivilegeIds(privilegeIds);

    if (!this.roleEntityAuthorizationService.isReadable(roles.stream().map(Role::id).toList())) {
      throw new AccessDeniedException("Not allowed to read roles");
    }

    return roles;
  }
}
