package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
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
import java.util.Objects;
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

    String roleId = Objects.requireNonNull(role.id(), "id was validated");

    this.checkAccessControl(role, roleId, userIds);

    if (this.repository.get(roleId).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, role with ID {} already exists.", roleId);
      }
      return RestoreRoleResult.skipped(roleId, "Role with ID " + roleId + " already exists");
    }

    this.validateAnchor(role);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("restore role: {}", role);
    }

    Instant timestamp = Instant.now(this.clock);

    RoleSnapshot snapshot = new RoleSnapshot(role, userIds, privilegeIds);

    this.repository.restore(role);
    if (!userIds.isEmpty()) {
      this.roleAssigner.assignRolesToUsers(userIds, List.of(roleId));
    }
    if (!privilegeIds.isEmpty()) {
      this.roleAssigner.assignPrivilegesToRoles(List.of(roleId), privilegeIds);
    }

    return RestoreRoleResult.restored(roleId, snapshot, timestamp);
  }

  private void validateRole(Role role) {
    if (role.id() == null || role.id().isBlank()) {
      throw new IllegalArgumentException("The id of the role must not be null or empty.");
    }
    if (role.name() == null || role.name().isBlank()) {
      throw new IllegalArgumentException("The name of the role must not be null or empty.");
    }
  }

  private void checkAccessControl(Role role, String roleId, List<String> userIds) {
    if (!this.roleEntityAuthorizationService.isCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }

    if (userIds != null
        && !userIds.isEmpty()
        && !this.roleEntityAuthorizationService.isWritable(roleId)) {
      throw new AccessDeniedException(
          "Not allowed to update user to create role " + role + " -> " + userIds);
    }
  }

  private void validateAnchor(Role role) {
    Anchor anchor = role.anchor();
    if (anchor != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(anchor);
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(anchor, owner);
          });
    }
  }
}
