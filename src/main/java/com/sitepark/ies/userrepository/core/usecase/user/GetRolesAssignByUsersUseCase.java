package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.util.List;

public class GetRolesAssignByUsersUseCase {
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  GetRolesAssignByUsersUseCase(RoleAssigner roleAssigner, AccessControl accessControl) {
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  public UserRoleAssignment getRolesAssignByUsers(List<String> userIds) {
    if (!this.accessControl.isUserReadable()) {
      throw new AccessDeniedException("Not allowed to read user assignments");
    }
    return this.roleAssigner.getRolesAssignByUsers(userIds);
  }
}
