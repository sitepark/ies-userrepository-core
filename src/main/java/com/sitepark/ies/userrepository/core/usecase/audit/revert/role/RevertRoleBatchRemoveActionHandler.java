package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.RoleSnapshot;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.RestoreRole;
import com.sitepark.ies.userrepository.core.usecase.role.RestoreRoleRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class RevertRoleBatchRemoveActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final RestoreRole restoreRoleUseCase;

  private final Clock clock;

  @Inject
  RevertRoleBatchRemoveActionHandler(
      AuditLogService auditLogService, RestoreRole restoreRoleUseCase, Clock clock) {
    this.auditLogService = auditLogService;
    this.restoreRoleUseCase = restoreRoleUseCase;
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
      RoleSnapshot restoreData;
      try {
        Optional<RoleSnapshot> dataOpt =
            this.auditLogService.getBackwardData(childId, RoleSnapshot.class);
        restoreData =
            dataOpt.orElseThrow(
                () -> new RevertFailedException(request, "No backward data for log " + childId));
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize role-snapshot", e);
      }

      this.restoreRoleUseCase.restoreRole(new RestoreRoleRequest(restoreData, auditLogParentId));
    }
  }

  private String createRevertBatchRemoveLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.ROLE.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_REMOVE.name(),
            null,
            null,
            now,
            null));
  }
}
