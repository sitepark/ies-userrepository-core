package com.sitepark.ies.userrepository.core.usecase.audit.revert;

import com.sitepark.ies.sharedkernel.audit.ReverseActionHandler;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserAssignRolesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserBatchAssignRolesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserBatchRemoveActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserBatchUnassignRolesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserCreateActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserRemoveActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserUnassignRolesActionHandler;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.user.RevertUserUpdateActionHandler;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class RevertUserActionHandler implements ReverseActionHandler {

  private final Map<String, RevertEntityActionHandler> actionHandlers;

  @Inject
  RevertUserActionHandler(
      RevertUserCreateActionHandler createHandler,
      RevertUserUpdateActionHandler updateHandler,
      RevertUserRemoveActionHandler removeHandler,
      RevertUserBatchRemoveActionHandler batchRemoveHandler,
      RevertUserAssignRolesActionHandler assignRolesHandler,
      RevertUserBatchAssignRolesActionHandler batchAssignRolesHandler,
      RevertUserUnassignRolesActionHandler unassignRolesHandler,
      RevertUserBatchUnassignRolesActionHandler batchUnassignRolesHandler) {
    this.actionHandlers = new HashMap<>();
    this.actionHandlers.put(AuditLogAction.CREATE.name(), createHandler);
    this.actionHandlers.put(AuditLogAction.UPDATE.name(), updateHandler);
    this.actionHandlers.put(AuditLogAction.REMOVE.name(), removeHandler);
    this.actionHandlers.put(AuditLogAction.BATCH_REMOVE.name(), batchRemoveHandler);
    this.actionHandlers.put(AuditLogAction.ASSIGN_ROLES.name(), assignRolesHandler);
    this.actionHandlers.put(AuditLogAction.BATCH_ASSIGN_ROLES.name(), batchAssignRolesHandler);
    this.actionHandlers.put(AuditLogAction.UNASSIGN_ROLES.name(), unassignRolesHandler);
    this.actionHandlers.put(AuditLogAction.BATCH_UNASSIGN_ROLES.name(), batchUnassignRolesHandler);
  }

  @Override
  public String getEntityType() {
    return AuditLogEntityType.USER.name();
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
