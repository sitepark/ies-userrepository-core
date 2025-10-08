package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.RemoveRoles;
import com.sitepark.ies.userrepository.core.usecase.role.RemoveRolesRequest;
import jakarta.inject.Inject;

public class RevertRoleCreateActionHandler implements RevertEntityActionHandler {

  private final RemoveRoles removeRolesUseCase;

  @Inject
  RevertRoleCreateActionHandler(RemoveRoles removeRolesUseCase) {
    this.removeRolesUseCase = removeRolesUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removeRolesUseCase.removeRoles(
        RemoveRolesRequest.builder().id(request.entityId()).build());
  }
}
