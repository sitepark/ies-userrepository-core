package com.sitepark.ies.userrepository.core.usecase.audit.revert.role;

import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.role.UpdateRoleRequest;
import com.sitepark.ies.userrepository.core.usecase.role.UpdateRoleUseCase;
import jakarta.inject.Inject;

public class RevertRoleUpdateActionHandler implements RevertEntityActionHandler {

  private final UpdateRoleUseCase updateRoleUseCase;

  private final PatchService<Role> patchService;

  private final RoleRepository repository;

  @Inject
  RevertRoleUpdateActionHandler(
      UpdateRoleUseCase updateRoleUseCase,
      PatchServiceFactory patchServiceFactory,
      RoleRepository repository) {
    this.updateRoleUseCase = updateRoleUseCase;
    this.patchService = patchServiceFactory.createPatchService(Role.class);
    this.repository = repository;
  }

  @Override
  public void revert(RevertRequest request) {
    PatchDocument patch = this.patchService.parsePatch(request.backwardData());
    Role role =
        this.repository
            .get(request.entityId())
            .orElseThrow(
                () -> new RevertFailedException(request, "Role not found: " + request.entityId()));
    Role patchedRole = this.patchService.applyPatch(role, patch);
    this.updateRoleUseCase.updateRole(UpdateRoleRequest.builder().role(patchedRole).build());
  }
}
