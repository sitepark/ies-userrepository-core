package com.sitepark.ies.userrepository.core.usecase.user;

import static com.sitepark.ies.userrepository.core.domain.entity.Role.BUILT_IN_ROLE_ID_ADMINISTRATOR;
import static com.sitepark.ies.userrepository.core.domain.entity.User.BUILT_IN_USER_ID_INITIAL_USER;

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

public final class UnassignRolesFromUsersUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleAssigner roleAssigner;
  private final UserEntityAuthorizationService userEntityAuthorizationService;
  private final Clock clock;

  @Inject
  UnassignRolesFromUsersUseCase(
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

  public UnassignRolesFromUsersResult unassignRolesFromUsers(
      UnassignRolesFromUsersRequest request) {

    if (request.isEmpty()) {
      return UnassignRolesFromUsersResult.skipped();
    }

    List<String> userIds =
        IdentifierResolver.create(this.userRepository).resolve(request.userIdentifiers());

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    if (!this.userEntityAuthorizationService.isWritable(userIds)) {
      throw new AccessDeniedException("Not allowed to update user to add roles");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("unassign roles from users({}) -> roles({})", userIds, roleIds);
    }

    UserRoleAssignment effectiveUnassignment = effectiveUnassignment(userIds, roleIds);
    if (effectiveUnassignment.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective unassignments found, skipping");
      }
      return UnassignRolesFromUsersResult.skipped();
    }

    this.roleAssigner.unassignRolesFromUsers(userIds, roleIds);

    Instant timestamp = Instant.now(this.clock);

    return UnassignRolesFromUsersResult.unassigned(effectiveUnassignment, timestamp);
  }

  private UserRoleAssignment effectiveUnassignment(List<String> userIds, List<String> roleIds) {

    UserRoleAssignment assignments = this.roleAssigner.getRolesAssignByUsers(userIds);

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    for (String userId : userIds) {
      List<String> effectiveRoleIds =
          roleIds.stream().filter(assignments.roleIds(userId)::contains).toList();
      if (BUILT_IN_USER_ID_INITIAL_USER.equals(userId)) {
        effectiveRoleIds =
            effectiveRoleIds.stream()
                .filter(Predicate.not(BUILT_IN_ROLE_ID_ADMINISTRATOR::equals))
                .toList();
      }
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(userId, effectiveRoleIds);
      }
    }

    return builder.build();
  }
}
