package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesResult;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a privilege creation operation.
 *
 * <p>This record contains information about the created privilege including:
 *
 * <ul>
 *   <li>The created privilege ID
 *   <li>The privilege snapshot (privilege data + role IDs)
 *   <li>The result of role assignments (if roles were assigned)
 *   <li>The timestamp when the creation occurred
 * </ul>
 *
 * @param privilegeId the ID of the created privilege
 * @param snapshot the snapshot of the created privilege including role IDs
 * @param roleAssignmentResult the result of role assignments (null if no roles were assigned)
 * @param timestamp the timestamp when the privilege was created
 */
public record CreatePrivilegeResult(
    @NotNull String privilegeId,
    @NotNull PrivilegeSnapshot snapshot,
    @Nullable AssignPrivilegesToRolesResult roleAssignmentResult,
    @NotNull Instant timestamp) {}
