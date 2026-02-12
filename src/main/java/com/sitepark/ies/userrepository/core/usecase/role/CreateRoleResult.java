package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a role creation operation.
 *
 * <p>This record contains information about the created role including:
 *
 * <ul>
 *   <li>The created role ID
 *   <li>The role snapshot (role data + user IDs + privilege IDs)
 *   <li>The result of privilege assignments (if privileges were assigned)
 *   <li>The timestamp when the creation occurred
 * </ul>
 *
 * @param roleId the ID of the created role
 * @param snapshot the snapshot of the created role including privilege IDs
 * @param privilegeAssignmentResult the result of privilege assignments (null if no privileges were
 *     assigned)
 * @param timestamp the timestamp when the role was created
 */
public record CreateRoleResult(
    @NotNull String roleId,
    @NotNull RoleSnapshot snapshot,
    @Nullable AssignPrivilegesToRolesResult privilegeAssignmentResult,
    @NotNull Instant timestamp) {}
