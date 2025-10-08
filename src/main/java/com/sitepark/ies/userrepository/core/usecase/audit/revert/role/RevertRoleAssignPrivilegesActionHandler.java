package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.UnassignPrivilegesFromRoles;
import com.sitepark.ies.userrepository.core.usecase.role.UnassignPrivilegesFromRolesRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;

public class RevertRoleAssignPrivilegesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final UnassignPrivilegesFromRoles unassignPrivilegesFromRolesUseCase;

  @Inject
  RevertRoleAssignPrivilegesActionHandler(
      AuditLogService auditLogService,
      UnassignPrivilegesFromRoles unassignPrivilegesFromRolesUseCase) {
    this.auditLogService = auditLogService;
    this.unassignPrivilegesFromRolesUseCase = unassignPrivilegesFromRolesUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    try {
      List<String> privilegeds =
          this.auditLogService.deserializeList(request.backwardData(), String.class);
      this.unassignPrivilegesFromRolesUseCase.unassignPrivilegesFromRoles(
          UnassignPrivilegesFromRolesRequest.builder()
              .roleId(request.entityId())
              .privilegeIds(privilegeds)
              .build());
    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize privilegeds", e);
    }
  }
}
