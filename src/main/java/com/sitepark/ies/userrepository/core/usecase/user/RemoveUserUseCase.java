package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Use case for removing a single user from the repository.
 *
 * <p>This use case handles the business logic for user removal, including:
 *
 * <ul>
 *   <li>Access control verification
 *   <li>Built-in user protection (e.g., administrator cannot be removed)
 *   <li>Snapshot creation for audit purposes
 *   <li>User deletion from repository
 * </ul>
 *
 * <p>The use case returns a {@link RemoveUserResult} which indicates whether the user was removed
 * or skipped.
 */
public final class RemoveUserUseCase {

  private final UserRepository repository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final Clock clock;

  private static final String BUILT_IN_USER_ID_ADMINISTRATOR = "1";

  @Inject
  RemoveUserUseCase(
      UserRepository repository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      Clock clock) {

    this.repository = repository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.clock = clock;
  }

  /**
   * Removes a single user from the repository.
   *
   * @param request the removal request containing the user identifier
   * @return the removal result (either Removed or Skipped)
   * @throws AccessDeniedException if the current user is not allowed to remove users
   * @throws com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException if the
   *     user does not exist
   */
  public RemoveUserResult removeUser(RemoveUserRequest request) {

    if (!this.accessControl.isUserRemovable()) {
      throw new AccessDeniedException("Not allowed to remove user " + request.identifier());
    }

    String id = this.resolveIdentifier(request.identifier());

    // Skip built-in administrator
    if (BUILT_IN_USER_ID_ADMINISTRATOR.equals(id)) {
      return RemoveUserResult.skipped(id, "Built-in administrator cannot be removed");
    }

    // Create snapshot BEFORE removal (for audit)
    User user = this.loadUser(id);
    List<String> roleIds = this.roleAssigner.getRolesAssignByUser(id);
    UserSnapshot snapshot = new UserSnapshot(user, roleIds);
    Instant timestamp = Instant.now(this.clock);

    // Perform removal
    this.repository.remove(id);

    return RemoveUserResult.removed(id, user.toDisplayName(), snapshot, timestamp);
  }

  private String resolveIdentifier(com.sitepark.ies.sharedkernel.base.Identifier identifier) {
    IdentifierResolver resolver = IdentifierResolver.create(this.repository);
    return resolver.resolve(identifier);
  }

  private User loadUser(String id) {
    return this.repository
        .get(id)
        .orElseThrow(
            () ->
                new com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException(
                    "User with id " + id + " not found."));
  }
}
