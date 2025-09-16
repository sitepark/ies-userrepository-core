package com.sitepark.ies.userrepository.core.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.sharedkernel.audit.ReversibleAuditHandler;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import jakarta.inject.Inject;

public class ReversiblePrivilegeAuditHandler implements ReversibleAuditHandler {

  private final ObjectMapper objectMapper;

  private final RestorePrivilege restorePrivilegeUseCase;

  @Inject
  ReversiblePrivilegeAuditHandler(
      RestorePrivilege restorePrivilegeUseCase, ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.restorePrivilegeUseCase = restorePrivilegeUseCase;
  }

  @Override
  public String getEntityType() {
    return AuditLogEntityType.PRIVILEGE.name();
  }

  @Override
  public void revert(RevertRequest request) {
    if (AuditLogAction.REMOVE.name().equals(request.action())) {
      this.revertDelete(request);
    } else {
      throw new IllegalArgumentException("Unsupported action: " + request.action());
    }
  }

  private void revertDelete(RevertRequest request) {
    Privilege privilege = this.deserializePrivilege(request, request.oldData());

    this.restorePrivilegeUseCase.restorePrivilege(
        new RestorePrivilegeRequest(privilege, privilege.roleIds().toArray(new String[0]), null));
  }

  private Privilege deserializePrivilege(RevertRequest request, String json) {
    try {
      return this.objectMapper.readValue(json, Privilege.class);
    } catch (JsonProcessingException e) {
      throw new RevertFailedException(request, "Failed to deserialize privilege", e);
    }
  }
}
