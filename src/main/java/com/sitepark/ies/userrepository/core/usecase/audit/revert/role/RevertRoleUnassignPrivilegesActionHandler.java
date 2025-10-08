package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRoles;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesRequest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;

public class RevertRoleUnassignPrivilegesActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final AssignPrivilegesToRoles assignPrivilegesToRolesUseCase;

  @Inject
  RevertRoleUnassignPrivilegesActionHandler(
      AuditLogService auditLogService, AssignPrivilegesToRoles assignPrivilegesToRolesUseCase) {
    this.auditLogService = auditLogService;
    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    try {
      List<String> privilegeIds =
          this.auditLogService.deserializeList(request.backwardData(), String.class);
      this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
          AssignPrivilegesToRolesRequest.builder()
              .roleId(request.entityId())
              .privilegeIds(privilegeIds)
              .build());
    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize privilegeIds", e);
    }
  }
}
