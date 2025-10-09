package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.RemoveRolesRequest;
import com.sitepark.ies.userrepository.core.usecase.role.RemoveRolesUseCase;
import jakarta.inject.Inject;

public class RevertRoleCreateActionHandler implements RevertEntityActionHandler {

  private final RemoveRolesUseCase removeRolesUseCase;

  @Inject
  RevertRoleCreateActionHandler(RemoveRolesUseCase removeRolesUseCase) {
    this.removeRolesUseCase = removeRolesUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removeRolesUseCase.removeRoles(
        RemoveRolesRequest.builder().identifiers(b -> b.id(request.entityId())).build());
  }
}
