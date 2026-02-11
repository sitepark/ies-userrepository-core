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
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AssignPrivilegesToRolesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  @Inject
  AssignPrivilegesToRolesUseCase(
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

  public AssignPrivilegesToRolesResult assignPrivilegesToRoles(
      AssignPrivilegesToRolesRequest request) {

    if (request.isEmpty()) {
      return AssignPrivilegesToRolesResult.skipped();
    }

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());

    if (!this.roleEntityAuthorizationService.isWritable(roleIds)) {
      throw new AccessDeniedException("Not allowed to update roles to add privileges");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign privileges to roles({}) -> privileges({})", roleIds, privilegeIds);
    }

    RolePrivilegeAssignment effectiveAssignments = effectiveAssignments(roleIds, privilegeIds);

    if (effectiveAssignments.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective assignments found, skipping");
      }
      return AssignPrivilegesToRolesResult.skipped();
    }

    this.roleAssigner.assignPrivilegesToRoles(roleIds, privilegeIds);

    Instant timestamp = Instant.now(this.clock);

    return AssignPrivilegesToRolesResult.assigned(effectiveAssignments, timestamp);
  }

  private RolePrivilegeAssignment effectiveAssignments(
      List<String> roleIds, List<String> privilegeIds) {

    RolePrivilegeAssignment assignments = this.roleAssigner.getPrivilegesAssignByRoles(roleIds);

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    for (String roleId : roleIds) {
      List<String> effectivePrivilegeIds =
          privilegeIds.stream()
              .filter(Predicate.not(assignments.privilegeIds(roleId)::contains))
              .toList();
      if (!effectivePrivilegeIds.isEmpty()) {
        builder.assignments(roleId, effectivePrivilegeIds);
      }
    }

    return builder.build();
  }
}
