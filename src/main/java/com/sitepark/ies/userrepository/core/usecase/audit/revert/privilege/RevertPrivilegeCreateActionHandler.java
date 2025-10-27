package com.sitepark.ies.userrepository.core.usecase.audit.revert.privilege;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.privilege.RemovePrivilegesRequest;
import com.sitepark.ies.userrepository.core.usecase.privilege.RemovePrivilegesUseCase;
import jakarta.inject.Inject;

public class RevertPrivilegeCreateActionHandler implements RevertEntityActionHandler {

  private final RemovePrivilegesUseCase removePrivilegesUseCase;

  @Inject
  RevertPrivilegeCreateActionHandler(RemovePrivilegesUseCase removePrivilegesUseCase) {
    this.removePrivilegesUseCase = removePrivilegesUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removePrivilegesUseCase.removePrivileges(
        RemovePrivilegesRequest.builder().identifiers(b -> b.id(request.entityId())).build());
  }
}
