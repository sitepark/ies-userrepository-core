package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.RemoveUsersRequest;
import com.sitepark.ies.userrepository.core.usecase.user.RemoveUsersUseCase;
import jakarta.inject.Inject;

public class RevertUserCreateActionHandler implements RevertEntityActionHandler {

  private final RemoveUsersUseCase removeUsersUseCase;

  @Inject
  RevertUserCreateActionHandler(RemoveUsersUseCase removeUsersUseCase) {
    this.removeUsersUseCase = removeUsersUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removeUsersUseCase.removeUsers(
        RemoveUsersRequest.builder().identifiers(b -> b.add(request.entityId())).build());
  }
}
