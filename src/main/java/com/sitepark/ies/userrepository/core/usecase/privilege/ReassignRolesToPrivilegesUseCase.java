package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeRoleAssignment;
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

public final class ReassignRolesToPrivilegesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final PrivilegeEntityAuthorizationService privilegeEntityAuthorizationService;
  private final Clock clock;

  @Inject
  ReassignRolesToPrivilegesUseCase(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      RoleAssigner roleAssigner,
      PrivilegeEntityAuthorizationService privilegeEntityAuthorizationService,
      Clock clock) {

    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.roleAssigner = roleAssigner;
    this.privilegeEntityAuthorizationService = privilegeEntityAuthorizationService;
    this.clock = clock;
  }

  public ReassignRolesToPrivilegesResult reassignRolesToPrivileges(
      ReassignRolesToPrivilegesRequest request) {

    if (request.isEmpty()) {
      return ReassignRolesToPrivilegesResult.skipped();
    }

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());
    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.privilegeIdentifiers());

    if (!this.privilegeEntityAuthorizationService.isWritable(privilegeIds)) {
      throw new AccessDeniedException("Not allowed to update privileges to reassign roles");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("reassign roles to privileges({}) -> roles({})", privilegeIds, roleIds);
    }

    return this.reassignRolesToPrivileges(privilegeIds, roleIds);
  }

  private ReassignRolesToPrivilegesResult reassignRolesToPrivileges(
      List<String> privilegeIds, List<String> roleIds) {
    PrivilegeRoleAssignment assignments =
        this.roleAssigner.getRolesAssignByPrivileges(privilegeIds);
    PrivilegeRoleAssignment effectiveUnassignment =
        effectiveUnassignment(privilegeIds, roleIds, assignments);
    PrivilegeRoleAssignment effectiveAssignments =
        effectiveAssignments(privilegeIds, roleIds, assignments);

    if (effectiveAssignments.isEmpty() && effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective reassignments found, skipping");
      }
      return ReassignRolesToPrivilegesResult.skipped();
    }

    for (String privilegeId : effectiveUnassignment.privilegeIds()) {
      this.roleAssigner.unassignPrivilegesFromRoles(
          effectiveUnassignment.roleIds(privilegeId), List.of(privilegeId));
    }

    for (String privilegeId : effectiveAssignments.privilegeIds()) {
      this.roleAssigner.assignPrivilegesToRoles(
          effectiveAssignments.roleIds(privilegeId), List.of(privilegeId));
    }

    Instant timestamp = Instant.now(this.clock);

    return ReassignRolesToPrivilegesResult.reassigned(
        effectiveAssignments, effectiveUnassignment, timestamp);
  }

  private PrivilegeRoleAssignment effectiveAssignments(
      List<String> privilegeIds, List<String> roleIds, PrivilegeRoleAssignment assignments) {

    PrivilegeRoleAssignment.Builder builder = PrivilegeRoleAssignment.builder();

    for (String privilegeId : privilegeIds) {
      List<String> assignedRoles = assignments.roleIds(privilegeId);
      List<String> effectiveRoleIds =
          roleIds.stream().filter(Predicate.not(assignedRoles::contains)).toList();
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(privilegeId, effectiveRoleIds);
      }
    }

    return builder.build();
  }

  private PrivilegeRoleAssignment effectiveUnassignment(
      List<String> privilegeIds, List<String> roleIds, PrivilegeRoleAssignment assignments) {

    PrivilegeRoleAssignment.Builder builder = PrivilegeRoleAssignment.builder();

    for (String privilegeId : privilegeIds) {

      List<String> assignedRoles = assignments.roleIds(privilegeId);
      List<String> effectiveRoleIds =
          assignedRoles.stream().filter(Predicate.not(roleIds::contains)).toList();
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(privilegeId, effectiveRoleIds);
      }
    }

    return builder.build();
  }
}
