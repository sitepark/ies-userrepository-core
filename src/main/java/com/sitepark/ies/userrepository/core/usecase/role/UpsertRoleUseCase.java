package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;

public final class UpsertRoleUseCase {

  private final RoleRepository repository;
  private final CreateRoleUseCase createRoleUseCase;
  private final UpdateRoleUseCase updateRoleUseCase;

  @Inject
  UpsertRoleUseCase(
      RoleRepository repository,
      CreateRoleUseCase createRoleUseCase,
      UpdateRoleUseCase updateRoleUseCase) {
    this.repository = repository;
    this.createRoleUseCase = createRoleUseCase;
    this.updateRoleUseCase = updateRoleUseCase;
  }

  public UpsertRoleResult upsertRole(UpsertRoleRequest request) {

    Role roleResolved = this.toRoleWithId(request.role());
    if (roleResolved.id() == null) {
      CreateRoleResult result =
          this.createRoleUseCase.createRole(
              CreateRoleRequest.builder()
                  .role(roleResolved)
                  .privilegeIdentifiers(b -> b.identifiers(request.privilegeIdentifiers()))
                  .build());
      return new UpsertRoleResult.Created(result.roleId(), result);
    } else {
      UpdateRoleResult result =
          this.updateRoleUseCase.updateRole(
              UpdateRoleRequest.builder()
                  .role(roleResolved)
                  .privilegeIdentifiers(b -> b.identifiers(request.privilegeIdentifiers()))
                  .build());
      return new UpsertRoleResult.Updated(roleResolved.id(), result);
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
