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

public final class AssignRolesToUsersUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleAssigner roleAssigner;
  private final UserEntityAuthorizationService userEntityAuthorizationService;
  private final Clock clock;

  @Inject
  AssignRolesToUsersUseCase(
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

  public AssignRolesToUsersResult assignRolesToUsers(AssignRolesToUsersRequest request) {

    if (request.isEmpty()) {
      return AssignRolesToUsersResult.skipped();
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

    UserRoleAssignment effectiveAssignments = effectiveAssignments(userIds, roleIds);

    if (effectiveAssignments.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective assignments found, skipping");
      }
      return AssignRolesToUsersResult.skipped();
    }

    this.roleAssigner.assignRolesToUsers(userIds, roleIds);

    Instant timestamp = Instant.now(this.clock);

    return AssignRolesToUsersResult.assigned(effectiveAssignments, timestamp);
  }

  private UserRoleAssignment effectiveAssignments(List<String> userIds, List<String> rolesIds) {

    UserRoleAssignment assignments = this.roleAssigner.getRolesAssignByUsers(userIds);

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    for (String userId : userIds) {
      List<String> effectiveRoleIds =
          rolesIds.stream().filter(Predicate.not(assignments.roleIds(userId)::contains)).toList();
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(userId, effectiveRoleIds);
      }
    }

    return builder.build();
  }
}
