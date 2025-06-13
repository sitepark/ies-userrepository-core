package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CreatePrivilege {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  CreatePrivilege(
      PrivilegeRepository repository, RoleAssigner roleAssigner, AccessControl accessControl) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  public String createPrivilege(@NotNull Privilege privilege, @Nullable String[] roleIds) {

    if (privilege.getId() != null) {
      throw new IllegalArgumentException("The ID of the privilege must not be set when creating.");
    }
    if (privilege.getName() == null || privilege.getName().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.getPermission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }

    if (!this.accessControl.isPrivilegeCreatable()) {
      throw new AccessDeniedException("Not allowed to create privilege " + privilege);
    }

    if (roleIds != null && roleIds.length > 0) {
      if (!this.accessControl.isRoleWritable()) {
        throw new AccessDeniedException(
            "Not allowed to update role to add privilege "
                + privilege
                + " -> "
                + Arrays.toString(roleIds));
      }
    }

    this.validateAnchor(privilege);

    this.repository.validatePermission(privilege.getPermission());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create privilege: {}", privilege);
    }

    String id = this.repository.create(privilege);
    if (roleIds != null && roleIds.length > 0) {
      this.roleAssigner.assignPrivilegesToRoles(Arrays.asList(roleIds), List.of(id));
    }

    return id;
  }

  private void validateAnchor(Privilege privilege) {
    if (privilege.getAnchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(privilege.getAnchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(privilege.getAnchor(), owner);
          });
    }
  }
}
