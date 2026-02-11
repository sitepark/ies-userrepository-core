package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RestorePrivilegeUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private final Clock clock;

  @Inject
  RestorePrivilegeUseCase(
      PrivilegeRepository repository,
      RoleAssigner roleAssigner,
      PrivilegeEntityAuthorizationService privilegeAuthorizationService,
      Clock clock) {
    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.privilegeAuthorizationService = privilegeAuthorizationService;
    this.clock = clock;
  }

  public RestorePrivilegeResult restorePrivilege(RestorePrivilegeRequest request) {

    Privilege privilege = request.data().privilege();
    List<String> roleIds = request.data().roleIds();

    this.validatePrivilege(privilege);

    this.checkAuthorization(privilege);

    if (this.repository.get(privilege.id()).isPresent()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip restore, privilege with ID {} already exists.", privilege.id());
      }
      return RestorePrivilegeResult.skipped(
          privilege.id(), "Privilege with ID " + privilege.id() + " already exists");
    }

    this.validateAnchor(privilege);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("restore privilege: {}", privilege);
    }

    Instant timestamp = Instant.now(this.clock);

    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, roleIds);

    this.repository.restore(privilege);
    if (!roleIds.isEmpty()) {
      String privilegeId = privilege.id();
      assert privilegeId != null : "privilege.id() was validated in validatePrivilege()";
      this.roleAssigner.assignPrivilegesToRoles(roleIds, List.of(privilegeId));
    }

    return RestorePrivilegeResult.restored(privilege.id(), snapshot, timestamp);
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.id() == null || privilege.id().isBlank()) {
      throw new IllegalArgumentException("The id of the privilege must not be null or empty.");
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
      Optional<String> anchorOwner = this.repository.resolveAnchor(privilege.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(privilege.anchor(), owner);
          });
    }
  }
}
