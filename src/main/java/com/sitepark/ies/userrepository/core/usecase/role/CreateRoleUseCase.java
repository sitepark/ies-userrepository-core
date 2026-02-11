package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreateRoleUseCase {

  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  CreateRoleUseCase(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase,
      RoleEntityAuthorizationService roleEntityAuthorizationService,
      Clock clock) {
    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
    this.clock = clock;
  }

  public CreateRoleResult createRole(CreateRoleRequest request) {

    this.validateRole(request.role());

    this.checkAccessControl(request.role());

    this.validateAnchor(request.role());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create role: {}", request.role());
    }

    Instant timestamp = Instant.now(this.clock);

    String id = this.roleRepository.create(request.role());

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());

    Role createdRole = request.role().toBuilder().id(id).build();
    RoleSnapshot snapshot = new RoleSnapshot(createdRole, List.of(), privilegeIds);

    AssignPrivilegesToRolesResult privilegeAssignmentResult = null;
    if (!privilegeIds.isEmpty()) {
      privilegeAssignmentResult =
          this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
              AssignPrivilegesToRolesRequest.builder()
                  .roleIdentifiers(b -> b.id(id))
                  .privilegeIdentifiers(b -> b.ids(privilegeIds))
                  .build());
    }

    return new CreateRoleResult(id, snapshot, privilegeAssignmentResult, timestamp);
  }

  private void validateRole(Role role) {
    if (role.id() != null) {
      throw new IllegalArgumentException("The ID of the role must not be set when creating.");
    }
    if (role.name() == null || role.name().isBlank()) {
      throw new IllegalArgumentException("The name of the role must not be null or empty.");
    }
  }

  private void checkAccessControl(Role role) {
    if (!this.roleEntityAuthorizationService.isCreatable()) {
      throw new AccessDeniedException("Not allowed to create role " + role);
    }
  }

  private void validateAnchor(Role role) {
    if (role.anchor() != null) {
      Optional<String> anchorOwner = this.roleRepository.resolveAnchor(role.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(role.anchor(), owner);
          });
    }
  }
}
