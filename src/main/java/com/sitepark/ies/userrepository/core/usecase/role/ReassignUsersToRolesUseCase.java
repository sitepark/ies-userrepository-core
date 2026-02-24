package com.sitepark.ies.userrepository.core.usecase.role;

import static com.sitepark.ies.userrepository.core.domain.entity.Role.BUILT_IN_ROLE_ID_ADMINISTRATOR;
import static com.sitepark.ies.userrepository.core.domain.entity.User.BUILT_IN_USER_ID_INITIAL_USER;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RoleUserAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReassignUsersToRolesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  @Inject
  ReassignUsersToRolesUseCase(
      UserRepository userRepository,
      RoleRepository roleRepository,
      RoleAssigner roleAssigner,
      RoleEntityAuthorizationService roleEntityAuthorizationService,
      Clock clock) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.roleAssigner = roleAssigner;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
    this.clock = clock;
  }

  public ReassignUsersToRolesResult reassignUsersToRoles(ReassignUsersToRolesRequest request) {

    List<String> userIds =
        IdentifierResolver.create(this.userRepository).resolve(request.userIdentifiers());
    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    if (!this.roleEntityAuthorizationService.isWritable(roleIds)) {
      throw new AccessDeniedException("Not allowed to update roles to add users");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign user to roles({}) -> users({})", roleIds, userIds);
    }

    return this.reassignUsersToRoles(roleIds, userIds);
  }

  private ReassignUsersToRolesResult reassignUsersToRoles(
      List<String> roleIds, List<String> userIds) {
    RoleUserAssignment assignments = this.roleAssigner.getUsersAssignByRoles(roleIds);
    RoleUserAssignment effectiveUnassignment = effectiveUnassignment(roleIds, userIds, assignments);
    RoleUserAssignment effectiveAssignments = effectiveAssignments(roleIds, userIds, assignments);

    if (effectiveAssignments.isEmpty() && effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective reassignments found, skipping");
      }
      return ReassignUsersToRolesResult.skipped();
    }

    for (String roleId : effectiveUnassignment.roleIds()) {
      this.roleAssigner.unassignRolesFromUsers(
          effectiveUnassignment.userIds(roleId), List.of(roleId));
    }

    for (String roleId : effectiveAssignments.roleIds()) {
      this.roleAssigner.assignRolesToUsers(effectiveAssignments.userIds(roleId), List.of(roleId));
    }

    Instant timestamp = Instant.now(this.clock);

    return ReassignUsersToRolesResult.reassigned(
        effectiveAssignments, effectiveUnassignment, timestamp);
  }

  private RoleUserAssignment effectiveAssignments(
      List<String> rolesIds, List<String> userIds, RoleUserAssignment assignments) {

    RoleUserAssignment.Builder builder = RoleUserAssignment.builder();

    for (String rolesId : rolesIds) {
      List<String> assignedUsers = assignments.userIds(rolesId);
      List<String> effectiveUserIds =
          userIds.stream().filter(Predicate.not(assignedUsers::contains)).toList();
      if (!effectiveUserIds.isEmpty()) {
        builder.assignments(rolesId, effectiveUserIds);
      }
    }

    return builder.build();
  }

  private RoleUserAssignment effectiveUnassignment(
      List<String> roleIds, List<String> userIds, RoleUserAssignment assignments) {

    RoleUserAssignment.Builder builder = RoleUserAssignment.builder();

    for (String roleId : roleIds) {

      List<String> assignedUser = assignments.userIds(roleId);
      List<String> effectiveUserIds =
          assignedUser.stream().filter(Predicate.not(userIds::contains)).toList();
      if (BUILT_IN_ROLE_ID_ADMINISTRATOR.equals(roleId)) {
        effectiveUserIds =
            effectiveUserIds.stream()
                .filter(Predicate.not(BUILT_IN_USER_ID_INITIAL_USER::equals))
                .toList();
      }
      if (!effectiveUserIds.isEmpty()) {
        builder.assignments(roleId, effectiveUserIds);
      }
    }

    return builder.build();
  }
}
