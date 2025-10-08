package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.UnassignPrivilegesFromRoles;
import com.sitepark.ies.userrepository.core.usecase.role.UnassignPrivilegesFromRolesRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class RevertRoleBatchAssignPrivilegesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final UnassignPrivilegesFromRoles unassignPrivilegesFromRolesUseCase;

  private final Clock clock;

  @Inject
  RevertRoleBatchAssignPrivilegesActionHandler(
      AuditLogService auditLogService,
      UnassignPrivilegesFromRoles unassignPrivilegesFromRolesUseCase,
      Clock clock) {
    this.auditLogService = auditLogService;
    this.unassignPrivilegesFromRolesUseCase = unassignPrivilegesFromRolesUseCase;
    this.clock = clock;
  }

  @Override
  public void revert(RevertRequest request) {
    List<String> childIds = this.auditLogService.getRecursiveChildIds(request.id());
    if (childIds.isEmpty()) {
      return;
    }

    Instant now = Instant.now(this.clock);
    String auditLogParentId = this.createRevertBatchAssignRolesLog(now);

    for (String childId : childIds) {
      List<String> privilegesIds;
      try {
        privilegesIds = this.auditLogService.getBackwardDataList(childId, String.class);
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize privilegeIds", e);
      }

      this.unassignPrivilegesFromRolesUseCase.unassignPrivilegesFromRoles(
          UnassignPrivilegesFromRolesRequest.builder()
              .roleId(request.entityId())
              .privilegeIds(privilegesIds)
              .auditParentId(auditLogParentId)
              .build());
    }
  }

  private String createRevertBatchAssignRolesLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.ROLE.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_ASSIGN_PRIVILEGES.name(),
            null,
            null,
            now,
            null));
  }
}
