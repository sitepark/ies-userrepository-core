package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.util.List;

public final class GetPrivilegesAssignByRolesUseCase {
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;

  @Inject
  GetPrivilegesAssignByRolesUseCase(
      RoleAssigner roleAssigner, RoleEntityAuthorizationService roleEntityAuthorizationService) {
    this.roleAssigner = roleAssigner;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
  }

  public RolePrivilegeAssignment getPrivilegesAssignByRoles(List<String> roleIds) {
    if (!this.roleEntityAuthorizationService.isReadable(roleIds)) {
      throw new AccessDeniedException("Not allowed to read role assignments");
    }
    return this.roleAssigner.getPrivilegesAssignByRoles(roleIds);
  }
}
