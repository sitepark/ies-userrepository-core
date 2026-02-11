package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RestoreRoleUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository repository;
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  @Inject
  RestoreRoleUseCase(
      RoleRepository repository,
      RoleAssigner roleAssigner,
      RoleEntityAuthorizationService roleEntityAuthorizationService,
      Clock clock) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
    this.clock = clock;
  }

  public RestoreRoleResult restoreRole(RestoreRoleRequest request) {

    Role role = request.data().role();
    List<String> userIds = request.data().userIds();
    List<String> privilegeIds = request.data().privilegesIds();

    this.validateRole(role);

    this.checkAccessControl(role, userIds);

    if (this.repository.get(role.id()).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, role with ID {} already exists.", role.id());
      }
      return RestoreRoleResult.skipped(role.id(), "Role with ID " + role.id() + " already exists");
    }

    this.validateAnchor(role);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("restore role: {}", role);
    }

    Instant timestamp = Instant.now(this.clock);

    RoleSnapshot snapshot = new RoleSnapshot(role, userIds, privilegeIds);

    this.repository.restore(role);
    if (!userIds.isEmpty()) {
      String roleId = role.id();
      assert roleId != null : "role.id() was validated in validateUser()";
      this.roleAssigner.assignRolesToUsers(userIds, List.of(roleId));
    }
    if (!privilegeIds.isEmpty()) {
      String roleId = role.id();
      assert roleId != null : "role.id() was validated in validateUser()";
      this.roleAssigner.assignPrivilegesToRoles(List.of(roleId), privilegeIds);
    }

    return RestoreRoleResult.restored(role.id(), snapshot, timestamp);
  }

  private void validateRole(Role role) {
    if (role.id() == null || role.id().isBlank()) {
      throw new IllegalArgumentException("The id of the role must not be null or empty.");
    }
    if (role.name() == null || role.name().isBlank()) {
      throw new IllegalArgumentException("The name of the role must not be null or empty.");
    }
  }

  private void checkAccessControl(Role role, List<String> userIds) {
    if (!this.roleEntityAuthorizationService.isCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }

    if (userIds != null
        && !userIds.isEmpty()
        && !this.roleEntityAuthorizationService.isWritable(role.id())) {
      throw new AccessDeniedException(
          "Not allowed to update user to create role " + role + " -> " + userIds);
    }
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(role.anchor(), owner);
          });
    }
  }
}
