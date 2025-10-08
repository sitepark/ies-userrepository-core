package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.RestoreUser;
import com.sitepark.ies.userrepository.core.usecase.user.RestoreUserRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class RevertUserBatchRemoveActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final RestoreUser restoreUserUseCase;

  private final Clock clock;

  @Inject
  RevertUserBatchRemoveActionHandler(
      AuditLogService auditLogService, RestoreUser restoreUserUseCase, Clock clock) {
    this.auditLogService = auditLogService;
    this.restoreUserUseCase = restoreUserUseCase;
    this.clock = clock;
  }

  @Override
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void revert(RevertRequest request) {
    List<String> childIds = this.auditLogService.getRecursiveChildIds(request.id());
    if (childIds.isEmpty()) {
      return;
    }

    Instant now = Instant.now(this.clock);
    String auditLogParentId = this.createRevertBatchRemoveLog(now);

    for (String childId : childIds) {
      UserSnapshot restoreData;
      try {
        Optional<UserSnapshot> dataOpt =
            this.auditLogService.getBackwardData(childId, UserSnapshot.class);
        restoreData =
            dataOpt.orElseThrow(
                () -> new RevertFailedException(request, "No backward data for log " + childId));
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize user-snapshot", e);
      }

      this.restoreUserUseCase.restoreUser(new RestoreUserRequest(restoreData, auditLogParentId));
    }
  }

  private String createRevertBatchRemoveLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_REMOVE.name(),
            null,
            null,
            now,
            null));
  }
}
