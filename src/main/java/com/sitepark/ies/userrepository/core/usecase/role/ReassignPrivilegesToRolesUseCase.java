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

public final class ReassignPrivilegesToRolesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  @Inject
  ReassignPrivilegesToRolesUseCase(
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

  public ReassignPrivilegesToRolesResult reassignPrivilegesToRoles(
      AssignPrivilegesToRolesRequest request) {

    if (request.isEmpty()) {
      return ReassignPrivilegesToRolesResult.skipped();
    }

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());

    if (!this.roleEntityAuthorizationService.isWritable(roleIds)) {
      throw new AccessDeniedException("Not allowed to update roles to reassign privileges");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("reassign privileges to roles({}) -> privileges({})", roleIds, privilegeIds);
    }

    return this.reassignPrivilegesToRoles(roleIds, privilegeIds);
  }

  private ReassignPrivilegesToRolesResult reassignPrivilegesToRoles(
      List<String> roleIds, List<String> privilegeIds) {
    RolePrivilegeAssignment assignments = this.roleAssigner.getPrivilegesAssignByRoles(roleIds);
    RolePrivilegeAssignment effectiveUnassignment =
        effectiveUnassignment(roleIds, privilegeIds, assignments);
    RolePrivilegeAssignment effectiveAssignments =
        effectiveAssignments(roleIds, privilegeIds, assignments);

    if (effectiveAssignments.isEmpty() && effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective reassignments found, skipping");
      }
      return ReassignPrivilegesToRolesResult.skipped();
    }

    for (String roleId : effectiveUnassignment.roleIds()) {
      this.roleAssigner.unassignPrivilegesFromRoles(
          List.of(roleId), effectiveUnassignment.privilegeIds(roleId));
    }

    for (String roleId : effectiveAssignments.roleIds()) {
      this.roleAssigner.assignPrivilegesToRoles(
          List.of(roleId), effectiveAssignments.privilegeIds(roleId));
    }

    Instant timestamp = Instant.now(this.clock);

    return ReassignPrivilegesToRolesResult.reassigned(
        effectiveAssignments, effectiveUnassignment, timestamp);
  }

  private RolePrivilegeAssignment effectiveAssignments(
      List<String> roleIds, List<String> privilegeIds, RolePrivilegeAssignment assignments) {

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    for (String roleId : roleIds) {
      List<String> assignedPrivileges = assignments.privilegeIds(roleId);
      List<String> effectivePrivilegeIds =
          privilegeIds.stream().filter(Predicate.not(assignedPrivileges::contains)).toList();
      if (!effectivePrivilegeIds.isEmpty()) {
        builder.assignments(roleId, effectivePrivilegeIds);
      }
    }

    return builder.build();
  }

  private RolePrivilegeAssignment effectiveUnassignment(
      List<String> roleIds, List<String> privilegeIds, RolePrivilegeAssignment assignments) {

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    for (String roleId : roleIds) {

      List<String> assignedPrivileges = assignments.privilegeIds(roleId);
      List<String> effectivePrivilegeIds =
          assignedPrivileges.stream().filter(Predicate.not(privilegeIds::contains)).toList();
      if (!effectivePrivilegeIds.isEmpty()) {
        builder.assignments(roleId, effectivePrivilegeIds);
      }
    }

    return builder.build();
  }
}
