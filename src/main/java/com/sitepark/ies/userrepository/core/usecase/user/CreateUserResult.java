package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.domain.value.UserSnapshot;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * Result of a user creation operation.
 *
 * <p>This record contains information about the created user including:
 *
 * <ul>
 *   <li>The created user ID
 *   <li>The user snapshot (user data + role IDs)
 *   <li>The result of role assignments (if roles were assigned)
 *   <li>The timestamp when the creation occurred
 * </ul>
 *
 * @param userId the ID of the created user
 * @param snapshot the snapshot of the created user including role IDs
 * @param roleAssignmentResult the result of role assignments (null if no roles were assigned)
 * @param timestamp the timestamp when the user was created
 */
public record CreateUserResult(
    String userId,
    UserSnapshot snapshot,
    @Nullable AssignRolesToUsersResult roleAssignmentResult,
    Instant timestamp) {}
