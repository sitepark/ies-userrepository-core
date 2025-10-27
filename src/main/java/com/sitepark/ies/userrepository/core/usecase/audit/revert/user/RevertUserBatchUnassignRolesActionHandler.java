package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.AssignRolesToUsersRequest;
import com.sitepark.ies.userrepository.core.usecase.user.AssignRolesToUsersUseCase;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class RevertUserBatchUnassignRolesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final AssignRolesToUsersUseCase assignRolesToUsersUseCase;

  private final Clock clock;

  @Inject
  RevertUserBatchUnassignRolesActionHandler(
      AuditLogService auditLogService,
      AssignRolesToUsersUseCase assignRolesToUsersUseCase,
      Clock clock) {
    this.auditLogService = auditLogService;
    this.assignRolesToUsersUseCase = assignRolesToUsersUseCase;
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
      List<String> roleIds;
      try {
        roleIds = this.auditLogService.getBackwardDataList(childId, String.class);
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize roleIds", e);
      }

      this.assignRolesToUsersUseCase.assignRolesToUsers(
          AssignRolesToUsersRequest.builder()
              .userIdentifiers(b -> b.id(request.entityId()))
              .roleIdentifiers(b -> b.ids(roleIds))
              .auditParentId(auditLogParentId)
              .build());
    }
  }

  private String createRevertBatchUnassignRolesLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_UNASSIGN_ROLES.name(),
            null,
            null,
            now,
            null));
  }
}
