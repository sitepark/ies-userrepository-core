package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesRequest;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesResult;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesUseCase;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreatePrivilegeUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository privilegeRepository;
  private final RoleRepository roleRepository;
  private final AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private final PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private final Clock clock;

  @Inject
  CreatePrivilegeUseCase(
      PrivilegeRepository privilegeRepository,
      RoleRepository roleRepository,
      AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase,
      PrivilegeEntityAuthorizationService privilegeAuthorizationService,
      Clock clock) {
    this.privilegeRepository = privilegeRepository;
    this.roleRepository = roleRepository;
    this.assignPrivilegesToRolesUseCase = assignPrivilegesToRolesUseCase;
    this.privilegeAuthorizationService = privilegeAuthorizationService;
    this.clock = clock;
  }

  public CreatePrivilegeResult createPrivilege(CreatePrivilegeRequest request) {

    this.validatePrivilege(request.privilege());

    this.checkAuthorization(request.privilege());

    this.validateAnchor(request.privilege());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create privilege: {}", request.privilege());
    }

    Instant timestamp = Instant.now(this.clock);

    String id = this.privilegeRepository.create(request.privilege());

    Privilege createdPrivilege = request.privilege().toBuilder().id(id).build();

    AssignPrivilegesToRolesResult roleAssignmentResult;
    List<String> roleIds;
    if (request.roleIdentifiers().shouldUpdate()) {
      roleIds =
          IdentifierResolver.create(this.roleRepository)
              .resolve(request.roleIdentifiers().getValue());

      if (!roleIds.isEmpty()) {
        roleAssignmentResult =
            this.assignPrivilegesToRolesUseCase.assignPrivilegesToRoles(
                AssignPrivilegesToRolesRequest.builder()
                    .privilegeIdentifiers(b -> b.id(id))
                    .roleIdentifiers(b -> b.ids(roleIds))
                    .build());
      } else {
        roleAssignmentResult = AssignPrivilegesToRolesResult.skipped();
      }
    } else {
      roleAssignmentResult = AssignPrivilegesToRolesResult.skipped();
      roleIds = List.of();
    }
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(createdPrivilege, roleIds);
    return new CreatePrivilegeResult(id, snapshot, roleAssignmentResult, timestamp);
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.id() != null) {
      throw new IllegalArgumentException("The ID of the privilege must not be set when creating.");
    }
    if (privilege.name() == null || privilege.name().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.permission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }
  }

  private void checkAuthorization(Privilege privilege) {
    if (!this.privilegeAuthorizationService.isCreatable()) {
      throw new AccessDeniedException("Not allowed to create privilege " + privilege);
    }
  }

  private void validateAnchor(Privilege privilege) {
    if (privilege.anchor() != null) {
      Optional<String> anchorOwner = this.privilegeRepository.resolveAnchor(privilege.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(privilege.anchor(), owner);
          });
    }
  }
}
