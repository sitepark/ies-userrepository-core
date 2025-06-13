package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RevokePrivilegesFromRoles {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  RevokePrivilegesFromRoles(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      RoleAssigner roleAssigner,
      AccessControl accessControl) {
    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  public void revokePrivilegesFromRoles(
      @NotNull List<Identifier> roleIdentifiers, @NotNull List<Identifier> privilegeIdentifiers) {

    if (roleIdentifiers.isEmpty() || privilegeIdentifiers.isEmpty()) {
      return;
    }

    List<String> roleIds =
        roleIdentifiers.stream()
            .map(
                role ->
                    role.resolveId(
                        (anchor) ->
                            this.roleRepository
                                .resolveAnchor(role.getAnchor())
                                .orElseThrow(
                                    () ->
                                        new IllegalArgumentException(
                                            "Role with anchor "
                                                + role.getAnchor()
                                                + " not found."))))
            .toList();

    List<String> privilegeIds =
        privilegeIdentifiers.stream()
            .map(
                privilege ->
                    privilege.resolveId(
                        (anchor) ->
                            this.privilegeRepository
                                .resolveAnchor(privilege.getAnchor())
                                .orElseThrow(
                                    () ->
                                        new IllegalArgumentException(
                                            "Privilege with anchor "
                                                + privilege.getAnchor()
                                                + " not found."))))
            .toList();

    if (!this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException("Not allowed to update roles to add privileges");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "assign privileges to roles({}) -> privileges({})", roleIds, privilegeIdentifiers);
    }

    this.roleAssigner.revokePrivilegesFromRoles(roleIds, privilegeIds);
  }
}
