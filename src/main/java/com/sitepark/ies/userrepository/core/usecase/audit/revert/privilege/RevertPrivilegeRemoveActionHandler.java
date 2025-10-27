package com.sitepark.ies.userrepository.core.usecase.audit.revert.privilege;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.usecase.audit.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.privilege.RestorePrivilegeRequest;
import com.sitepark.ies.userrepository.core.usecase.privilege.RestorePrivilegeUseCase;
import jakarta.inject.Inject;
import java.io.IOException;

public class RevertPrivilegeRemoveActionHandler implements RevertEntityActionHandler {

  private final AuditLogService auditLogService;

  private final RestorePrivilegeUseCase restorePrivilegeUseCase;

  @Inject
  RevertPrivilegeRemoveActionHandler(
      AuditLogService auditLogService, RestorePrivilegeUseCase restorePrivilegeUseCase) {
    this.auditLogService = auditLogService;
    this.restorePrivilegeUseCase = restorePrivilegeUseCase;
  }

  @Override
  public void revert(RevertRequest request) {
    try {
      PrivilegeSnapshot restoreData =
          this.auditLogService.deserialize(request.backwardData(), PrivilegeSnapshot.class);
      this.restorePrivilegeUseCase.restorePrivilege(new RestorePrivilegeRequest(restoreData, null));
    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize privilege-snapshot", e);
    }
  }
}
