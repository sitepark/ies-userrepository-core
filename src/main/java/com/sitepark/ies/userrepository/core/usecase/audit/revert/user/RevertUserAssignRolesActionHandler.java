package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.UnassignRolesFromUsers;
import com.sitepark.ies.userrepository.core.usecase.user.UnassignRolesFromUsersRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;

public class RevertUserAssignRolesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final UnassignRolesFromUsers unassignRolesFromUsersUseCase;

  @Inject
  RevertUserAssignRolesActionHandler(
      AuditLogService auditLogService, UnassignRolesFromUsers unassignRolesFromUsersUseCase) {
    this.auditLogService = auditLogService;
    this.unassignRolesFromUsersUseCase = unassignRolesFromUsersUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    try {
      List<String> roleIds =
          this.auditLogService.deserializeList(request.backwardData(), String.class);
      this.unassignRolesFromUsersUseCase.unassignRolesFromUsers(
          UnassignRolesFromUsersRequest.builder()
              .userId(request.entityId())
              .roleIds(roleIds)
              .build());
    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize roleIds", e);
    }
  }
}
