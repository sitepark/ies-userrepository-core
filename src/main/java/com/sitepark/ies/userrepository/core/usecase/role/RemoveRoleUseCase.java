package com.sitepark.ies.userrepository.core.usecase.role;

import static com.sitepark.ies.userrepository.core.domain.entity.Role.BUILT_IN_ROLE_ID_ADMINISTRATOR;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Use case for removing a single role from the repository.
 *
 * <p>This use case handles the business logic for role removal, including:
 *
 * <ul>
 *   <li>Access control verification
 *   <li>Built-in role protection (e.g., administrator role cannot be removed)
 *   <li>Snapshot creation for audit purposes
 *   <li>Role deletion from repository
 * </ul>
 *
 * <p>The use case returns a {@link RemoveRoleResult} which indicates whether the role was removed
 * or skipped.
 */
public final class RemoveRoleUseCase {

  private final RoleRepository repository;
  private final RoleAssigner roleAssigner;
  private final RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock clock;

  @Inject
  RemoveRoleUseCase(
      RoleRepository repository,
      RoleAssigner roleAssigner,
      RoleEntityAuthorizationService roleEntityAuthorizationService,
      Clock clock) {

    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.roleEntityAuthorizationService = roleEntityAuthorizationService;
    this.clock = clock;
  }

  /**
   * Removes a single role from the repository.
   *
   * @param request the removal request containing the role identifier
   * @return the removal result (either Removed or Skipped)
   * @throws AccessDeniedException if the current user is not allowed to remove roles
   * @throws IllegalArgumentException if the role does not exist
   */
  public RemoveRoleResult removeRole(RemoveRoleRequest request) {

    String id = this.resolveIdentifier(request.identifier());

    // Skip built-in administrator role
    if (BUILT_IN_ROLE_ID_ADMINISTRATOR.equals(id)) {
      return RemoveRoleResult.skipped(id, "Built-in administrator role cannot be removed");
    }

    if (!this.roleEntityAuthorizationService.isRemovable(id)) {
      throw new AccessDeniedException("Not allowed to remove role " + request.identifier());
    }

    // Create snapshot BEFORE removal (for audit)
    Role role = this.loadRole(id);
    List<String> userIds = this.roleAssigner.getUsersAssignByRole(id);
    List<String> privilegeIds = this.roleAssigner.getPrivilegesAssignByRole(id);
    RoleSnapshot snapshot = new RoleSnapshot(role, userIds, privilegeIds);
    Instant timestamp = Instant.now(this.clock);

    // Perform removal
    this.repository.remove(id);

    return RemoveRoleResult.removed(id, role.name(), snapshot, timestamp);
  }

  private String resolveIdentifier(com.sitepark.ies.sharedkernel.base.Identifier identifier) {
    IdentifierResolver resolver = IdentifierResolver.create(this.repository);
    return resolver.resolve(identifier);
  }

  private Role loadRole(String id) {
    return this.repository
        .get(id)
        .orElseThrow(() -> new IllegalArgumentException("Role with id " + id + " not found."));
  }
}
