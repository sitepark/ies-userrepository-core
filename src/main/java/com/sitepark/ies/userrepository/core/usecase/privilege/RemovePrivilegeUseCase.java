package com.sitepark.ies.userrepository.core.usecase.privilege;

import static com.sitepark.ies.userrepository.core.domain.entity.Privilege.BUILT_IN_PRIVILEGE_ID_FULL_ACCESS;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Use case for removing a single privilege from the repository.
 *
 * <p>This use case handles the business logic for privilege removal, including:
 *
 * <ul>
 *   <li>Access control verification
 *   <li>Built-in privilege protection (e.g., FULL_ACCESS cannot be removed)
 *   <li>Snapshot creation for audit purposes
 *   <li>Privilege deletion from repository
 * </ul>
 *
 * <p>The use case returns a {@link RemovePrivilegeResult} which indicates whether the privilege was
 * removed or skipped.
 */
public final class RemovePrivilegeUseCase {

  private final PrivilegeRepository repository;
  private final RoleAssigner roleAssigner;
  private final PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private final Clock clock;

  @Inject
  RemovePrivilegeUseCase(
      PrivilegeRepository repository,
      RoleAssigner roleAssigner,
      PrivilegeEntityAuthorizationService privilegeAuthorizationService,
      Clock clock) {

    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.privilegeAuthorizationService = privilegeAuthorizationService;
    this.clock = clock;
  }

  /**
   * Removes a single privilege from the repository.
   *
   * @param request the removal request containing the privilege identifier
   * @return the removal result (either Removed or Skipped)
   * @throws AccessDeniedException if the current user is not allowed to remove privileges
   * @throws IllegalArgumentException if the privilege does not exist
   */
  public RemovePrivilegeResult removePrivilege(RemovePrivilegeRequest request) {

    String id = this.resolveIdentifier(request.identifier());

    // Skip built-in FULL_ACCESS privilege
    if (BUILT_IN_PRIVILEGE_ID_FULL_ACCESS.equals(id)) {
      return RemovePrivilegeResult.skipped(id, "Built-in privilege FULL_ACCESS cannot be removed");
    }

    if (!this.privilegeAuthorizationService.isRemovable(id)) {
      throw new AccessDeniedException("Not allowed to remove privilege " + request.identifier());
    }

    // Create snapshot BEFORE removal (for audit)
    Privilege privilege = this.loadPrivilege(id);
    List<String> roleIds = this.roleAssigner.getRolesAssignByPrivilege(id);
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, roleIds);
    Instant timestamp = Instant.now(this.clock);

    // Perform removal
    this.repository.remove(id);

    return RemovePrivilegeResult.removed(id, privilege.name(), snapshot, timestamp);
  }

  private String resolveIdentifier(com.sitepark.ies.sharedkernel.base.Identifier identifier) {
    IdentifierResolver resolver = IdentifierResolver.create(this.repository);
    return resolver.resolve(identifier);
  }

  private Privilege loadPrivilege(String id) {
    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("Privilege with id " + id + " not found."));
  }
}
