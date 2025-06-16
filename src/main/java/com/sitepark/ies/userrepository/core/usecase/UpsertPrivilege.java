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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UpsertPrivilege {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  UpsertPrivilege(
      PrivilegeRepository repository, RoleAssigner roleAssigner, AccessControl accessControl) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  @SuppressWarnings("PMD.UseVarargs")
  public String upsertPrivilege(@NotNull Privilege privilege, @Nullable String[] roleIds) {

    if (privilege.getPermission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }

    if (roleIds != null && roleIds.length > 0 && !this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException(
          "Not allowed to update role to add privilege "
              + privilege
              + " -> "
              + Arrays.toString(roleIds));
    }

    Privilege privilegeResolved = this.toPrivilegeWithId(privilege);
    if (privilegeResolved.getId() == null) {
      return this.create(privilegeResolved, roleIds);
    } else {
      return this.update(privilegeResolved, roleIds);
    }
  }

  @SuppressWarnings("PMD.UseVarargs")
  private String create(Privilege privilege, String[] roleIds) {
    if (!this.accessControl.isPrivilegeCreatable()) {
      throw new AccessDeniedException("Not allowed to create privilege " + privilege);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("upsert(create) privilege: {}", privilege);
    }
    String id = this.repository.create(privilege);
    if (roleIds != null && roleIds.length > 0) {
      this.roleAssigner.assignPrivilegesToRoles(Arrays.asList(roleIds), List.of(id));
    }
    return id;
  }

  @SuppressWarnings("PMD.UseVarargs")
  private String update(Privilege privilege, String[] roleIds) {
    if (!this.accessControl.isPrivilegeWritable()) {
      throw new AccessDeniedException("Not allowed to create privilege " + privilege);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("upsert(update) privilege: {}", privilege);
    }
    this.repository.update(privilege);
    if (roleIds != null && roleIds.length > 0) {
      this.roleAssigner.reassignPrivilegesToRoles(
          Arrays.asList(roleIds), List.of(privilege.getId()));
    }
    return privilege.getId();
  }

  private Privilege toPrivilegeWithId(Privilege privilege) {
    if (privilege.getId() == null && privilege.getAnchor() != null) {
      return this.repository
          .resolveAnchor(privilege.getAnchor())
          .map(s -> privilege.toBuilder().id(s).build())
          .orElse(privilege);
    } else if (privilege.getId() != null && privilege.getAnchor() != null) {
      this.repository
          .resolveAnchor(privilege.getAnchor())
          .ifPresent(
              owner -> {
                if (!owner.equals(privilege.getId())) {
                  throw new AnchorAlreadyExistsException(privilege.getAnchor(), owner);
                }
              });
    }
    return privilege;
  }
}
