package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.UnassignRolesFromUsersRequest;
import com.sitepark.ies.userrepository.core.usecase.user.UnassignRolesFromUsersUseCase;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class RevertUserBatchAssignRolesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final UnassignRolesFromUsersUseCase unassignRolesFromUsersUseCase;

  private final Clock clock;

  @Inject
  RevertUserBatchAssignRolesActionHandler(
      AuditLogService auditLogService,
      UnassignRolesFromUsersUseCase unassignRolesFromUsersUseCase,
      Clock clock) {
    this.auditLogService = auditLogService;
    this.unassignRolesFromUsersUseCase = unassignRolesFromUsersUseCase;
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
      List<String> roleIds;
      try {
        roleIds = this.auditLogService.getBackwardDataList(childId, String.class);
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize roleIds", e);
      }

      this.unassignRolesFromUsersUseCase.unassignRolesFromUsers(
          UnassignRolesFromUsersRequest.builder()
              .userIdentifiers(b -> b.id(request.entityId()))
              .roleIdentifiers(b -> b.ids(roleIds))
              .auditParentId(auditLogParentId)
              .build());
    }
  }

  private String createRevertBatchAssignRolesLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_ASSIGN_ROLES.name(),
            null,
            null,
            now,
            null));
  }
}
