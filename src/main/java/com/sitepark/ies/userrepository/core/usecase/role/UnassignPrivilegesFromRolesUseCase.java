package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UnassignPrivilegesFromRolesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  @Inject
  UnassignPrivilegesFromRolesUseCase(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      RoleAssigner roleAssigner,
      RoleEntityAuthorizationService roleEntityAuthorizationService,
      Clock clock) {
    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.roleAssigner = roleAssigner;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
    this.clock = clock;
  }

  public UnassignPrivilegesFromRolesResult unassignPrivilegesFromRoles(
      UnassignPrivilegesFromRolesRequest request) {

    if (request.isEmpty()) {
      return UnassignPrivilegesFromRolesResult.skipped();
    }

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());

    if (!this.roleEntityAuthorizationService.isWritable(roleIds)) {
      throw new AccessDeniedException("Not allowed to update roles to remove privileges");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("unassign privileges from roles({}) -> privileges({})", roleIds, privilegeIds);
    }

    RolePrivilegeAssignment effectiveUnassignments = effectiveUnassignments(roleIds, privilegeIds);

    if (effectiveUnassignments.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective unassignments found, skipping");
      }
      return UnassignPrivilegesFromRolesResult.skipped();
    }

    this.roleAssigner.unassignPrivilegesFromRoles(roleIds, privilegeIds);

    Instant timestamp = Instant.now(this.clock);

    return UnassignPrivilegesFromRolesResult.unassigned(effectiveUnassignments, timestamp);
  }

  private RolePrivilegeAssignment effectiveUnassignments(
      List<String> roleIds, List<String> privilegeIds) {

    RolePrivilegeAssignment assignments = this.roleAssigner.getPrivilegesAssignByRoles(roleIds);

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    for (String roleId : roleIds) {
      List<String> effectivePrivilegeIds =
          privilegeIds.stream().filter(assignments.privilegeIds(roleId)::contains).toList();
      if (!effectivePrivilegeIds.isEmpty()) {
        builder.assignments(roleId, effectivePrivilegeIds);
      }
    }

    return builder.build();
  }
}
