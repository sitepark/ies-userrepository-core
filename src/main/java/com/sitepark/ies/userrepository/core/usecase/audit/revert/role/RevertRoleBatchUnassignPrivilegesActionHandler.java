package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRoles;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class RevertRoleBatchUnassignPrivilegesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final AssignPrivilegesToRoles assignPrivilegesToRolesUseCase;

  private final Clock clock;

  @Inject
  RevertRoleBatchUnassignPrivilegesActionHandler(
      AuditLogService auditLogService,
      AssignPrivilegesToRoles assignPrivilegesToRolesUseCase,
      Clock clock) {
    this.auditLogService = auditLogService;
    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
    this.clock = clock;
  }

  @Override
  public void revert(RevertRequest request) {
    List<String> childIds = this.auditLogService.getRecursiveChildIds(request.id());
    if (childIds.isEmpty()) {
      return;
    }

    Instant now = Instant.now(this.clock);
    String auditLogParentId = this.createRevertBatchUnassignRolesLog(now);

    for (String childId : childIds) {
      List<String> privilegeIds;
      try {
        privilegeIds = this.auditLogService.getBackwardDataList(childId, String.class);
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize privilegeIds", e);
      }

      this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
          AssignPrivilegesToRolesRequest.builder()
              .roleId(request.entityId())
              .privilegeIds(privilegeIds)
              .auditParentId(auditLogParentId)
              .build());
    }
  }

  private String createRevertBatchUnassignRolesLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.ROLE.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_UNASSIGN_PRIVILEGES.name(),
            null,
            null,
            now,
            null));
  }
}
