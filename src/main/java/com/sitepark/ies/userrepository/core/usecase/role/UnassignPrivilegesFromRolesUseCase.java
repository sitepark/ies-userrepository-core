package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UnassignPrivilegesFromRolesUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;
  private final AuditLogService auditLogService;
  private final Clock clock;

  @Inject
  UnassignPrivilegesFromRolesUseCase(
      RoleRepository roleRepository,
      PrivilegeRepository privilegeRepository,
      RoleAssigner roleAssigner,
      AccessControl accessControl,
      AuditLogService auditLogService,
      Clock clock) {
    this.roleRepository = roleRepository;
    this.privilegeRepository = privilegeRepository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public void unassignPrivilegesFromRoles(UnassignPrivilegesFromRolesRequest request) {

    if (request.isEmpty()) {
      return;
    }

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    List<String> privilegeIds =
        IdentifierResolver.create(this.privilegeRepository).resolve(request.privilegeIdentifiers());

    if (!this.accessControl.isRoleWritable()) {
      throw new AccessDeniedException("Not allowed to update roles to add privileges");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign privileges to roles({}) -> privileges({})", roleIds, privilegeIds);
    }

    RolePrivilegeAssignment effectiveUnassignments = effectiveUnassignments(roleIds, privilegeIds);

    if (effectiveUnassignments.isEmpty()) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("no effective unassignments found, skipping");
      }
      return;
    }

    this.roleAssigner.unassignPrivilegesFromRoles(roleIds, privilegeIds);

    Instant now = Instant.now(this.clock);
    String parentId =
        effectiveUnassignments.size() > 1
            ? createBatchAssignmentLog(now, request.auditParentId())
            : request.auditParentId();

    effectiveUnassignments
        .roleIds()
        .forEach(
            roleId -> {
              CreateAuditLogRequest createAuditLogRequest =
                  buildCreateAuditLogRequest(
                      roleId, effectiveUnassignments.privilegeIds(roleId), now, parentId);
              this.auditLogService.createAuditLog(createAuditLogRequest);
            });
  }

  private RolePrivilegeAssignment effectiveUnassignments(
      List<String> roleIds, List<String> privilegeIds) {

    RolePrivilegeAssignment assignments = this.roleAssigner.getPrivilegesAssignByRoles(roleIds);

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    for (String roleId : roleIds) {
      List<String> effectivePrivilegeIds =
          privilegeIds.stream().filter(assignments.privilegeIds(roleId)::contains).toList();
      if (!effectivePrivilegeIds.isEmpty()) {
        builder.assignments(roleId, effectivePrivilegeIds);
      }
    }

    return builder.build();
  }

  private String createBatchAssignmentLog(Instant now, String parentId) {
    return this.auditLogService.createAuditLog(
        new CreateAuditLogRequest(
            AuditLogEntityType.ROLE.name(),
            null,
            null,
            AuditLogAction.BATCH_UNASSIGN_PRIVILEGES.name(),
            null,
            null,
            now,
            parentId));
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      String roleId, List<String> privilegesIds, Instant now, String parentId) {

    String roleName = this.roleRepository.get(roleId).map(Role::name).orElse(null);

    String privilegesJsonArray;
    try {
      privilegesJsonArray = this.auditLogService.serialize(privilegesIds);
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.ROLE.name(), roleId, roleName, e);
    }

    return new CreateAuditLogRequest(
        AuditLogEntityType.ROLE.name(),
        roleId,
        roleName,
        AuditLogAction.UNASSIGN_PRIVILEGES.name(),
        privilegesJsonArray,
        privilegesJsonArray,
        now,
        parentId);
  }
}
