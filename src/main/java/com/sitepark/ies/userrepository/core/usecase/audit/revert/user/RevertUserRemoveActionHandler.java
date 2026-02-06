package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.RestoreUserRequest;
import com.sitepark.ies.userrepository.core.usecase.user.RestoreUserResult;
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

      RestoreUserResult result =
          this.restoreUserUseCase.restoreUser(new RestoreUserRequest(restoreData, null));

      if (result instanceof RestoreUserResult.Restored restored) {
        this.createRestoreAuditLog(restored, null);
      }

    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize user-snapshot", e);
    }
  }

  private void createRestoreAuditLog(RestoreUserResult.Restored restored, String auditParentId) {
    String forwardData;
    try {
      forwardData = this.auditLogService.serialize(restored.snapshot());
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.USER.name(),
          restored.userId(),
          restored.snapshot().user().toDisplayName(),
          e);
    }

    CreateAuditLogRequest createAuditLogRequest =
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            restored.userId(),
            restored.snapshot().user().toDisplayName(),
            AuditLogAction.RESTORE.name(),
            null,
            forwardData,
            restored.timestamp(),
            auditParentId);

    this.auditLogService.createAuditLog(createAuditLogRequest);
  }
}
