package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UpsertRole {

  private final AccessControl accessControl;
  private final RoleRepository repository;
  private final CreateRole createRoleUseCase;
  private final UpdateRole updateRoleUseCase;

  @Inject
  UpsertRole(
      AccessControl accessControl,
      RoleRepository repository,
      CreateRole createRoleUseCase,
      UpdateRole updateRoleUseCase) {
    this.accessControl = accessControl;
    this.repository = repository;
    this.createRoleUseCase = createRoleUseCase;
    this.updateRoleUseCase = updateRoleUseCase;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String upsertRole(@NotNull Role role, @Nullable String[] privilegeIds) {

    if (!this.accessControl.isRoleCreatable() || !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException("Not allowed to upsert role " + role);
    }

    Role roleResolved = this.toRoleWithId(role);
    if (roleResolved.id() == null) {
      return this.createRoleUseCase.createRole(roleResolved, privilegeIds);
    } else {
      return this.updateRoleUseCase.updateRole(roleResolved, privilegeIds);
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
