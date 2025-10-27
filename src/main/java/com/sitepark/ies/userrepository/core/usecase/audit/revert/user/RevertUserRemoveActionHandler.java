package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.RestoreUserRequest;
import com.sitepark.ies.userrepository.core.usecase.user.RestoreUserUseCase;
import jakarta.inject.Inject;
import java.io.IOException;

public class RevertUserRemoveActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final RestoreUserUseCase restoreUserUseCase;

  @Inject
  RevertUserRemoveActionHandler(
      AuditLogService auditLogService, RestoreUserUseCase restoreUserUseCase) {
    this.auditLogService = auditLogService;
    this.restoreUserUseCase = restoreUserUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    try {
      UserSnapshot restoreData =
          this.auditLogService.deserialize(request.backwardData(), UserSnapshot.class);
      this.restoreUserUseCase.restoreUser(new RestoreUserRequest(restoreData, null));
    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize user-snapshot", e);
    }
  }
}
