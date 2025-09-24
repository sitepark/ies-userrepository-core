package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AssignPrivilegesToRoles {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  AssignPrivilegesToRoles(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      RoleAssigner roleAssigner,
      AccessControl accessControl) {
    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  public void assignPrivilegesToRoles(AssignPrivilegesToRolesRequest request) {

    if (request.roleIdentifiers().isEmpty() || request.privilegeIdentifiers().isEmpty()) {
      return;
    }

    List<String> roleIds =
        request.roleIdentifiers().stream()
            .map(
                role ->
                    role.resolveId(
                        (anchor) ->
                            this.roleRepository
                                .resolveAnchor(role.getAnchor())
                                .orElseThrow(() -> new AnchorNotFoundException(anchor))))
            .toList();

    List<String> privilegeIds =
        request.privilegeIdentifiers().stream()
            .map(
                privilege ->
                    privilege.resolveId(
                        (anchor) ->
                            this.privilegeRepository
                                .resolveAnchor(privilege.getAnchor())
                                .orElseThrow(() -> new AnchorNotFoundException(anchor))))
            .toList();

    if (!this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException("Not allowed to update roles to add privileges");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign privileges to roles({}) -> privileges({})", roleIds, privilegeIds);
    }

    this.roleAssigner.assignPrivilegesToRoles(roleIds, privilegeIds);
  }
}
