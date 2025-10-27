package com.sitepark.ies.userrepository.core.usecase.audit.revert;

import com.sitepark.ies.sharedkernel.audit.ReverseActionHandler;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleAssignPrivilegesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleBatchRemoveActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleBatchUnassignPrivilegesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleCreateActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleRemoveActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleUnassignPrivilegesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.role.RevertRoleUpdateActionHandler;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class RevertRoleActionHandler implements ReverseActionHandler {

  private final Map<String, RevertEntityActionHandler> actionHandlers;

  @Inject
  RevertRoleActionHandler(
      RevertRoleCreateActionHandler createHandler,
      RevertRoleUpdateActionHandler updateHandler,
      RevertRoleRemoveActionHandler removeHandler,
      RevertRoleBatchRemoveActionHandler batchRemoveHandler,
      RevertRoleAssignPrivilegesActionHandler assignPrivilegesHandler,
      RevertRoleAssignPrivilegesActionHandler batchAssignPrivilegesHandler,
      RevertRoleUnassignPrivilegesActionHandler unassignPrivilegesActionHandler,
      RevertRoleBatchUnassignPrivilegesActionHandler batchUnassignPrivilegesActionHandler) {

    this.actionHandlers = new HashMap<>();
    this.actionHandlers.put(AuditLogAction.CREATE.name(), createHandler);
    this.actionHandlers.put(AuditLogAction.UPDATE.name(), updateHandler);
    this.actionHandlers.put(AuditLogAction.REMOVE.name(), removeHandler);
    this.actionHandlers.put(AuditLogAction.BATCH_REMOVE.name(), batchRemoveHandler);
    this.actionHandlers.put(AuditLogAction.ASSIGN_PRIVILEGES.name(), assignPrivilegesHandler);
    this.actionHandlers.put(
        AuditLogAction.BATCH_ASSIGN_PRIVILEGES.name(), batchAssignPrivilegesHandler);
    this.actionHandlers.put(
        AuditLogAction.UNASSIGN_PRIVILEGES.name(), unassignPrivilegesActionHandler);
    this.actionHandlers.put(
        AuditLogAction.BATCH_UNASSIGN_PRIVILEGES.name(), batchUnassignPrivilegesActionHandler);
  }

  @Override
  public String getEntityType() {
    return AuditLogEntityType.ROLE.name();
  }

  @Override
  public void revert(RevertRequest request) {
    RevertEntityActionHandler handler = this.actionHandlers.get(request.action());
    if (handler == null) {
      throw new IllegalArgumentException("Unsupported action: " + request.action());
    }
    handler.revert(request);
  }
}
