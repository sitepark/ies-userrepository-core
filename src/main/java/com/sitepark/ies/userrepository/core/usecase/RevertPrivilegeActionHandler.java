package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.audit.ReverseActionHandler;
import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.PrivilegeSnapshot;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class RevertPrivilegeActionHandler implements ReverseActionHandler {

  private final AuditLogService auditLogService;

  private final RestorePrivilege restorePrivilegeUseCase;

  private final UpdatePrivilege updatePrivilegeUseCase;

  private final RemovePrivileges removePrivilegesUseCase;

  private final PatchService<Privilege> patchService;

  private final PrivilegeRepository repository;

  private final Clock clock;

  @Inject
  RevertPrivilegeActionHandler(
      RestorePrivilege restorePrivilegeUseCase,
      UpdatePrivilege updatePrivilegeUseCase,
      RemovePrivileges removePrivilegesUseCase,
      AuditLogService auditLogService,
      PatchServiceFactory patchServiceFactory,
      PrivilegeRepository repository,
      Clock clock) {
    this.auditLogService = auditLogService;
    this.restorePrivilegeUseCase = restorePrivilegeUseCase;
    this.updatePrivilegeUseCase = updatePrivilegeUseCase;
    this.removePrivilegesUseCase = removePrivilegesUseCase;
    this.patchService = patchServiceFactory.createPatchService(Privilege.class);
    this.repository = repository;
    this.clock = clock;
  }

  @Override
  public String getEntityType() {
    return AuditLogEntityType.PRIVILEGE.name();
  }

  @Override
  public void revert(RevertRequest request) {
    if (AuditLogAction.CREATE.name().equals(request.action())) {
      this.revertCreate(request.entityId());
    } else if (AuditLogAction.UPDATE.name().equals(request.action())) {
      this.revertUpdate(request, request.backwardData());
    } else if (AuditLogAction.REMOVE.name().equals(request.action())) {
      this.revertRemove(request, request.backwardData());
    } else if (AuditLogAction.BATCH_REMOVE.name().equals(request.action())) {
      this.revertBatchRemove(request);
    } else {
      throw new IllegalArgumentException("Unsupported action: " + request.action());
    }
  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void revertBatchRemove(RevertRequest request) {

    List<String> childIds = this.auditLogService.getRecursiveChildIds(request.id());
    if (childIds.isEmpty()) {
      return;
    }

    Instant now = Instant.now(this.clock);
    String auditLogParentId = this.createRevertBatchRemoveLog(now);

    for (String childId : childIds) {
      PrivilegeSnapshot restoreData;
      try {
        Optional<PrivilegeSnapshot> dataOpt =
            this.auditLogService.getOldData(childId, PrivilegeSnapshot.class);
        restoreData =
            dataOpt.orElseThrow(
                () -> new RevertFailedException(request, "No old data for log " + childId));
      } catch (IOException e) {
        throw new RevertFailedException(request, "Failed to deserialize privilege", e);
      }

      this.restorePrivilegeUseCase.restorePrivilege(
          new RestorePrivilegeRequest(restoreData, auditLogParentId));
    }
  }

  private String createRevertBatchRemoveLog(Instant now) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.PRIVILEGE.name(),
            null,
            null,
            AuditLogAction.REVERT_BATCH_REMOVE.name(),
            null,
            null,
            now,
            null));
  }

  private void revertCreate(String privilegeId) {
    this.removePrivilegesUseCase.removePrivileges(
        RemovePrivilegesRequest.builder().id(privilegeId).build());
  }

  private void revertUpdate(RevertRequest request, String oldData) {
    PatchDocument patch = this.patchService.parsePatch(oldData);
    Privilege privilege =
        this.repository
            .get(request.entityId())
            .orElseThrow(
                () ->
                    new RevertFailedException(
                        request, "Privilege not found: " + request.entityId()));
    Privilege patchedPrivilege = this.patchService.applyPatch(privilege, patch);
    this.updatePrivilegeUseCase.updatePrivilege(
        UpdatePrivilegeRequest.builder().privilege(patchedPrivilege).build());
  }

  private void revertRemove(RevertRequest request, String oldData) {
    try {
      PrivilegeSnapshot restoreData =
          this.auditLogService.deserialize(oldData, PrivilegeSnapshot.class);
      this.revertRemove(restoreData);
    } catch (IOException e) {
      throw new RevertFailedException(request, "Failed to deserialize privilege", e);
    }
  }

  private void revertRemove(PrivilegeSnapshot restoreData) {
    this.restorePrivilegeUseCase.restorePrivilege(new RestorePrivilegeRequest(restoreData, null));
  }
}
