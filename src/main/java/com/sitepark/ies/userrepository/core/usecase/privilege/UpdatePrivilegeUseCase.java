package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.exception.PrivilegeNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdatePrivilegeUseCase {

  private static final Logger LOGGER = LogManager.getLogger();

  private final ReassignRolesToPrivilegesUseCase reassignRolesToPrivilegesUseCase;
  private final PrivilegeRepository repository;
  private final PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private final PatchService<Privilege> patchService;
  private final Clock clock;

  @Inject
  UpdatePrivilegeUseCase(
      ReassignRolesToPrivilegesUseCase reassignRolesToPrivilegesUseCase,
      PrivilegeRepository repository,
      PrivilegeEntityAuthorizationService privilegeAuthorizationService,
      PatchServiceFactory patchServiceFactory,
      Clock clock) {
    this.reassignRolesToPrivilegesUseCase = reassignRolesToPrivilegesUseCase;
    this.repository = repository;
    this.privilegeAuthorizationService = privilegeAuthorizationService;
    this.patchService = patchServiceFactory.createPatchService(Privilege.class);
    this.clock = clock;
  }

  public UpdatePrivilegeResult updatePrivilege(UpdatePrivilegeRequest request) {

    Privilege newPrivilege;
    if (request.privilege().id() == null) {
      newPrivilege = this.toPrivilegeWithId(request.privilege());
    } else {
      this.validateAnchor(request.privilege());
      newPrivilege = request.privilege();
    }

    this.validatePrivilege(newPrivilege);
    this.checkAuthorization(newPrivilege);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("update privilege: {}", request.privilege());
    }

    Privilege oldPrivilege =
        this.repository
            .get(newPrivilege.id())
            .orElseThrow(
                () ->
                    new PrivilegeNotFoundException(
                        "No privilege with ID " + newPrivilege.id() + " found."))
            .toBuilder()
            .build();

    Instant timestamp = Instant.now(this.clock);

    PatchDocument patch = this.patchService.createPatch(oldPrivilege, newPrivilege);
    PatchDocument revertPatch = null;

    if (patch.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Skip update, privilege with ID {} is unchanged.", newPrivilege.id());
      }
    } else {
      this.repository.update(newPrivilege);
      revertPatch = this.patchService.createPatch(newPrivilege, oldPrivilege);
    }

    ReassignRolesToPrivilegesResult roleReassignmentResult =
        this.reassignRolesToPrivilegesUseCase.reassignRolesToPrivileges(
            ReassignRolesToPrivilegesRequest.builder()
                .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers()))
                .privilegeIdentifiers(b -> b.id(newPrivilege.id()))
                .build());

    return new UpdatePrivilegeResult(
        newPrivilege.id(),
        newPrivilege.name(),
        timestamp,
        patch,
        revertPatch,
        roleReassignmentResult);
  }

  private void validatePrivilege(Privilege privilege) {
    if (privilege.name() == null || privilege.name().isBlank()) {
      throw new IllegalArgumentException("The name of the privilege must not be null or empty.");
    }
    if (privilege.permission() == null) {
      throw new IllegalArgumentException("The permission of the privilege must not be null.");
    }
  }

  private void checkAuthorization(Privilege privilege) {
    if (!this.privilegeAuthorizationService.isWritable(privilege.id())) {
      throw new AccessDeniedException("Not allowed to update privilege " + privilege);
    }
  }

  private Privilege toPrivilegeWithId(Privilege privilege) {
    if (privilege.id() == null) {
      if (privilege.anchor() != null) {
        String id =
            this.repository
                .resolveAnchor(privilege.anchor())
                .orElseThrow(() -> new AnchorNotFoundException(privilege.anchor()));
        return privilege.toBuilder().id(id).build();
      }
      throw new IllegalArgumentException(
          "Neither id nor anchor is specified to update the privilege.");
    }
    return privilege;
  }

  private void validateAnchor(Privilege privilege) {
    if (privilege.anchor() != null) {
      Optional<String> anchorOwner = this.repository.resolveAnchor(privilege.anchor());
      anchorOwner.ifPresent(
          owner -> {
            if (!owner.equals(privilege.id())) {
              throw new AnchorAlreadyExistsException(privilege.anchor(), owner);
            }
          });
    }
  }
}
