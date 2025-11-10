package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.util.List;

public final class GetPrivilegesAssignByRolesUseCase {
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  GetPrivilegesAssignByRolesUseCase(RoleAssigner roleAssigner, AccessControl accessControl) {
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  public RolePrivilegeAssignment getPrivilegesAssignByRoles(List<String> roleIds) {
    if (!this.accessControl.isRoleReadable()) {
      throw new AccessDeniedException("Not allowed to read role assignments");
    }
    return this.roleAssigner.getPrivilegesAssignByRoles(roleIds);
  }
}
