package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
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

public final class UpdatePrivilege {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  UpdatePrivilege(
      PrivilegeRepository repository, RoleAssigner roleAssigner, AccessControl accessControl) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String updatePrivilege(@NotNull Privilege privilege, @Nullable String[] roleIds) {

    this.validatePrivilege(privilege);

    this.checkAccessControl(privilege, roleIds);

    Privilege privilegeWithId = this.toPrivilegeWithId(privilege);

    this.validateAnchor(privilegeWithId);
    this.repository.validatePermission(privilegeWithId.getPermission());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update privilege: {}", privilege);
    }

    this.repository.update(privilegeWithId);

    if (roleIds != null && roleIds.length > 0) {
      this.roleAssigner.assignPrivilegesToRoles(
          Arrays.asList(roleIds), List.of(privilegeWithId.getId()));
    }

    return privilegeWithId.getId();
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.getName() == null || privilege.getName().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.getPermission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }
  }

  @SuppressWarnings("PMD.UseVarargs")
  private void checkAccessControl(Privilege privilege, @Nullable String[] roleIds) {
    if (!this.accessControl.isPrivilegeWritable()) {
      throw new AccessDeniedException("Not allowed to update privilege " + privilege);
    }

    if (roleIds != null && roleIds.length > 0 && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to add privilege "
              + privilege
              + " -> "
              + Arrays.toString(roleIds));
    }
  }

  private Privilege toPrivilegeWithId(Privilege privilege) {
    if (privilege.getId() == null) {
      if (privilege.getAnchor() != null) {
        String id =
            this.repository
                .resolveAnchor(privilege.getAnchor())
                .orElseThrow(() -> new AnchorNotFoundException(privilege.getAnchor()));
        return privilege.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException(
          "Neither id nor anchor is specified to update the privilege.");
    }
    return privilege;
  }

  private void validateAnchor(Privilege privilege) {
    if (privilege.getAnchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(privilege.getAnchor());
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(privilege.getId())) {
              throw new AnchorAlreadyExistsException(privilege.getAnchor(), owner);
            }
          });
    }
  }
}
