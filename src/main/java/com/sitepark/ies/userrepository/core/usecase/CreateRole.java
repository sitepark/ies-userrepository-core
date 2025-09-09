package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
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

public final class CreateRole {

  private final RoleRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  CreateRole(RoleRepository repository, RoleAssigner roleAssigner, AccessControl accessControl) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String createRole(@NotNull Role role, @Nullable String[] privilegeIds) {

    this.validateRole(role);

    this.checkAccessControl(role, privilegeIds);

    this.validateAnchor(role);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create role: {}", role);
    }

    String id = this.repository.create(role);
    if (privilegeIds != null && privilegeIds.length > 0) {
      this.roleAssigner.reassignPrivilegesToRoles(List.of(id), Arrays.asList(privilegeIds));
    }

    return id;
  }

  private void validateRole(Role role) {
    assert role.name() != null && !role.name().isBlank();
    if (role.id() != null) {
      throw new IllegalArgumentException("The ID of the privilege must not be set when creating.");
    }
  }

  @SuppressWarnings("PMD.UseVarargs")
  private void checkAccessControl(Role role, @Nullable String[] privilegeIds) {
    if (!this.accessControl.isRoleCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }

    if (privilegeIds != null && privilegeIds.length > 0 && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to add privilege "
              + role
              + " -> "
              + Arrays.toString(privilegeIds));
    }
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(role.anchor(), owner);
          });
    }
  }
}
