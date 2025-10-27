package com.sitepark.ies.userrepository.core.usecase.audit.revert.privilege;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.privilege.RestorePrivilegeRequest;
import com.sitepark.ies.userrepository.core.usecase.privilege.RestorePrivilegeUseCase;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class RevertPrivilegeBatchRemoveActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final RestorePrivilegeUseCase restorePrivilegeUseCase;

  private final Clock clock;

  @Inject
  RevertPrivilegeBatchRemoveActionHandler(
      AuditLogService auditLogService,
      RestorePrivilegeUseCase restorePrivilegeUseCase,
      Clock clock) {
    this.auditLogService = auditLogService;
    this.restorePrivilegeUseCase = restorePrivilegeUseCase;
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
      PrivilegeSnapshot restoreData;
      try {
        Optional<PrivilegeSnapshot> dataOpt =
            this.auditLogService.getBackwardData(childId, PrivilegeSnapshot.class);
        restoreData =
            dataOpt.orElseThrow(
                () -> new RevertFailedException(request, "No backward data for log " + childId));
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize privilege-snapshot", e);
      }

      this.restorePrivilegeUseCase.restorePrivilege(
          new RestorePrivilegeRequest(restoreData, auditLogParentId));
    }
  }

  private String createRevertBatchRemoveLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.PRIVILEGE.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_REMOVE.name(),
            null,
            null,
            now,
            null));
  }
}
