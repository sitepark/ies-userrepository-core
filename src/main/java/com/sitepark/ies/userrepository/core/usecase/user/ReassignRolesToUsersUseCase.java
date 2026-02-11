package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
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

public final class ReassignRolesToUsersUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleAssigner roleAssigner;
  private final UserEntityAuthorizationService userEntityAuthorizationService;
  private final Clock clock;

  @Inject
  ReassignRolesToUsersUseCase(
      UserRepository userRepository,
      RoleRepository roleRepository,
      RoleAssigner roleAssigner,
      UserEntityAuthorizationService userEntityAuthorizationService,
      Clock clock) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.roleAssigner = roleAssigner;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
    this.clock = clock;
  }

  public ReassignRolesToUsersResult reassignRolesToUsers(AssignRolesToUsersRequest request) {

    if (request.isEmpty()) {
      return ReassignRolesToUsersResult.skipped();
    }

    List<String> userIds =
        IdentifierResolver.create(this.userRepository).resolve(request.userIdentifiers());
    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    if (!this.userEntityAuthorizationService.isWritable(userIds)) {
      throw new AccessDeniedException("Not allowed to update users to add roles");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign roles to users({}) -> roles({})", userIds, roleIds);
    }

    return this.reassignRolesToUsers(userIds, roleIds);
  }

  private ReassignRolesToUsersResult reassignRolesToUsers(
      List<String> userIds, List<String> roleIds) {
    UserRoleAssignment assignments = this.roleAssigner.getRolesAssignByUsers(userIds);
    UserRoleAssignment effectiveUnassignment = effectiveUnassignment(userIds, roleIds, assignments);
    UserRoleAssignment effectiveAssignments = effectiveAssignments(userIds, roleIds, assignments);

    if (effectiveAssignments.isEmpty() && effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective reassignments found, skipping");
      }
      return ReassignRolesToUsersResult.skipped();
    }

    for (String userId : effectiveUnassignment.userIds()) {
      this.roleAssigner.unassignRolesFromUsers(
          List.of(userId), effectiveUnassignment.roleIds(userId));
    }

    for (String userId : effectiveAssignments.userIds()) {
      this.roleAssigner.assignRolesToUsers(List.of(userId), effectiveAssignments.roleIds(userId));
    }

    Instant timestamp = Instant.now(this.clock);

    return ReassignRolesToUsersResult.reassigned(
        effectiveAssignments, effectiveUnassignment, timestamp);
  }

  private UserRoleAssignment effectiveAssignments(
      List<String> userIds, List<String> rolesIds, UserRoleAssignment assignments) {

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    for (String userId : userIds) {
      List<String> assignedRoles = assignments.roleIds(userId);
      List<String> effectiveRoleIds =
          rolesIds.stream().filter(Predicate.not(assignedRoles::contains)).toList();
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(userId, effectiveRoleIds);
      }
    }

    return builder.build();
  }

  private UserRoleAssignment effectiveUnassignment(
      List<String> userIds, List<String> roleIds, UserRoleAssignment assignments) {

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    for (String userId : userIds) {

      List<String> assignedRoles = assignments.roleIds(userId);
      List<String> effectiveRoleIds =
          assignedRoles.stream().filter(Predicate.not(roleIds::contains)).toList();
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(userId, effectiveRoleIds);
      }
    }

    return builder.build();
  }
}
