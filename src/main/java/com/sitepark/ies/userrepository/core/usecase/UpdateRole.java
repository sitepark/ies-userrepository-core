package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UpdateRole {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  UpdateRole(RoleRepository repository, RoleAssigner roleAssigner, AccessControl accessControl) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String updateRole(@NotNull Role role, @Nullable String[] privilegeIds) {

    this.validateRole(role);

    this.checkAccessControl(role);

    Role roleWithId = this.toRoleWithId(role);

    this.validateAnchor(roleWithId);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update role: {}", roleWithId);
    }

    this.repository.update(roleWithId);

    if (privilegeIds != null && privilegeIds.length > 0) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(
            "reassign privileges to roles: {} -> {}",
            Arrays.asList(privilegeIds),
            List.of(roleWithId.id()));
      }
      this.roleAssigner.reassignPrivilegesToRoles(
          List.of(roleWithId.id()), Arrays.asList(privilegeIds));
    }

    return roleWithId.id();
  }

  private void validateRole(Role role) {
    assert role.name() != null && !role.name().isBlank();
  }

  private void checkAccessControl(Role role) {
    if (!this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException("Not allowed to update role " + role);
    }
  }

  private Role toRoleWithId(Role role) {
    if (role.id() == null) {
      if (role.anchor() != null) {
        String id =
            this.repository
                .resolveAnchor(role.anchor())
                .orElseThrow(() -> new AnchorNotFoundException(role.anchor()));
        return role.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException("Neither id nor anchor is specified to update the role.");
    }
    return role;
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(role.id())) {
              throw new AnchorAlreadyExistsException(role.anchor(), owner);
            }
          });
    }
  }
}
