package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.RemoveUserRequest;
import com.sitepark.ies.userrepository.core.usecase.user.RemoveUserUseCase;
import jakarta.inject.Inject;

public class RevertUserCreateActionHandler implements RevertEntityActionHandler {

  private final RemoveUserUseCase removeUserUseCase;

  @Inject
  RevertUserCreateActionHandler(RemoveUserUseCase removeUserUseCase) {
    this.removeUserUseCase = removeUserUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    this.removeUserUseCase.removeUser(RemoveUserRequest.builder().id(request.entityId()).build());
  }
}
