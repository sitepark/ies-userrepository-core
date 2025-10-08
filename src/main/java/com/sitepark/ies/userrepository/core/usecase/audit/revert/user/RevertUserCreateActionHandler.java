package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.RemoveUsers;
import com.sitepark.ies.userrepository.core.usecase.user.RemoveUsersRequest;
import jakarta.inject.Inject;

public class RevertUserCreateActionHandler implements RevertEntityActionHandler {

  private final RemoveUsers removeUsersUseCase;

  @Inject
  RevertUserCreateActionHandler(RemoveUsers removeUsersUseCase) {
    this.removeUsersUseCase = removeUsersUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removeUsersUseCase.removeUsers(
        RemoveUsersRequest.builder().id(request.entityId()).build());
  }
}
