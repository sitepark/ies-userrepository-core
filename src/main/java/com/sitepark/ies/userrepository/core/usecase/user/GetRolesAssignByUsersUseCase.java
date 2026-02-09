package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.util.List;

public final class GetRolesAssignByUsersUseCase {
  private final RoleAssigner roleAssigner;
  private final UserEntityAuthorizationService userEntityAuthorizationService;

  @Inject
  GetRolesAssignByUsersUseCase(
      RoleAssigner roleAssigner, UserEntityAuthorizationService userEntityAuthorizationService) {
    this.roleAssigner = roleAssigner;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
  }

  public UserRoleAssignment getRolesAssignByUsers(List<String> userIds) {
    if (!this.userEntityAuthorizationService.isReadable(userIds)) {
      throw new AccessDeniedException("Not allowed to read user assignments");
    }
    return this.roleAssigner.getRolesAssignByUsers(userIds);
  }
}
