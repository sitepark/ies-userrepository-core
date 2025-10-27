package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;

public final class UpsertRoleUseCase {

  private final AccessControl accessControl;
  private final RoleRepository repository;
  private final CreateRoleUseCase createRoleUseCase;
  private final UpdateRoleUseCase updateRoleUseCase;

  @Inject
  UpsertRoleUseCase(
      AccessControl accessControl,
      RoleRepository repository,
      CreateRoleUseCase createRoleUseCase,
      UpdateRoleUseCase updateRoleUseCase) {
    this.accessControl = accessControl;
    this.repository = repository;
    this.createRoleUseCase = createRoleUseCase;
    this.updateRoleUseCase = updateRoleUseCase;
  }

  public String upsertRole(UpsertRoleRequest request) {

    if (!this.accessControl.isRoleCreatable() || !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException("Not allowed to upsert role " + request.role());
    }

    Role roleResolved = this.toRoleWithId(request.role());
    if (roleResolved.id() == null) {
      return this.createRoleUseCase.createRole(
          CreateRoleRequest.builder()
              .role(roleResolved)
              .privilegeIdentifiers(b -> b.identifiers(request.privilegeIdentifiers()))
              .auditParentId(request.auditParentId())
              .build());
    } else {
      return this.updateRoleUseCase.updateRole(
          UpdateRoleRequest.builder()
              .role(roleResolved)
              .privilegeIdentifiers(b -> b.identifiers(request.privilegeIdentifiers()))
              .auditParentId(request.auditParentId())
              .build());
    }
  }

  private Role toRoleWithId(Role role) {
    if (role.id() == null && role.anchor() != null) {
      return this.repository
          .resolveAnchor(role.anchor())
          .map(s -> role.toBuilder().id(s).build())
          .orElse(role);
    } else if (role.id() != null && role.anchor() != null) {
      this.repository
          .resolveAnchor(role.anchor())
          .ifPresent(
              owner -> {
                if (!owner.equals(role.id())) {
                  throw new AnchorAlreadyExistsException(role.anchor(), owner);
                }
              });
    }
    return role;
  }
}
