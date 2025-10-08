package com.sitepark.ies.userrepository.core.usecase.audit.revert.privilege;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.privilege.RemovePrivileges;
import com.sitepark.ies.userrepository.core.usecase.privilege.RemovePrivilegesRequest;
import jakarta.inject.Inject;

public class RevertPrivilegeCreateActionHandler implements RevertEntityActionHandler {

  private final RemovePrivileges removePrivilegesUseCase;

  @Inject
  RevertPrivilegeCreateActionHandler(RemovePrivileges removePrivilegesUseCase) {
    this.removePrivilegesUseCase = removePrivilegesUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removePrivilegesUseCase.removePrivileges(
        RemovePrivilegesRequest.builder().id(request.entityId()).build());
  }
}
