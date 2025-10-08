package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UnassignRolesFromUsers {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  @Inject
  UnassignRolesFromUsers(
      UserRepository userRepository,
      RoleRepository roleRepository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      AuditLogService auditLogService,
      Clock clock) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public void unassignRolesFromUsers(UnassignRolesFromUsersRequest request) {

    if (request.userIdentifiers().isEmpty() || request.roleIdentifiers().isEmpty()) {
      return;
    }

    List<String> userIds =
        request.userIdentifiers().stream()
            .map(
                user ->
                    user.resolveId(
                        (anchor) ->
                            this.userRepository
                                .resolveAnchor(user.getAnchor())
                                .orElseThrow(() -> new AnchorNotFoundException(anchor))))
            .toList();

    List<String> roleIds =
        request.roleIdentifiers().stream()
            .map(
                role ->
                    role.resolveId(
                        (anchor) ->
                            this.roleRepository
                                .resolveAnchor(role.getAnchor())
                                .orElseThrow(() -> new AnchorNotFoundException(anchor))))
            .toList();

    if (!this.accessControl.isUserWritable()) {
      throw new AccessDeniedException("Not allowed to update user to add roles");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign roles to users({}) -> roles({})", userIds, roleIds);
    }

    UserRoleAssignment effectiveUnassignments = effectiveUnassignments(userIds, roleIds);
    if (effectiveUnassignments.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective unassignments found, skipping");
      }
      return;
    }

    this.roleAssigner.unassignRolesFromUsers(userIds, roleIds);

    Instant now = Instant.now(this.clock);
    String parentId =
        effectiveUnassignments.size() > 1
            ? createBatchAssignmentLog(now, request.auditParentId())
            : request.auditParentId();

    effectiveUnassignments
        .getUserIds()
        .forEach(
            userId -> {
              CreateAuditLogRequest createAuditLogRequest =
                  buildCreateAuditLogRequest(
                      userId, effectiveUnassignments.getRoleIds(userId), now, parentId);
              this.auditLogService.createAuditLog(createAuditLogRequest);
            });
  }

  private UserRoleAssignment effectiveUnassignments(List<String> userIds, List<String> roleIds) {

    UserRoleAssignment assignments = this.roleAssigner.getRolesAssignByUsers(userIds);

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    for (String userId : userIds) {
      List<String> effectiveRoleIds =
          roleIds.stream().filter(assignments.getRoleIds(userId)::contains).toList();
      if (!effectiveRoleIds.isEmpty()) {
        builder.assignments(userId, effectiveRoleIds);
      }
    }

    return builder.build();
  }

  private String createBatchAssignmentLog(Instant now, String parentId) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            null,
            null,
            AuditLogAction.BATCH_UNASSIGN_ROLES.name(),
            null,
            null,
            now,
            parentId));
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      String userId, List<String> roleIds, Instant now, String parentId) {

    String userDisplayName = this.userRepository.get(userId).map(User::toDisplayName).orElse(null);

    String rolesJsonArray;
    try {
      rolesJsonArray = this.auditLogService.serialize(roleIds);
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.USER.name(), userId, userDisplayName, e);
    }

    return new CreateAuditLogRequest(
        AuditLogEntityType.USER.name(),
        userId,
        userDisplayName,
        AuditLogAction.UNASSIGN_ROLES.name(),
        rolesJsonArray,
        rolesJsonArray,
        now,
        parentId);
  }
}
