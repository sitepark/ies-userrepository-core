package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveRolesTest {

  private RoleRepository repository;
  private AccessControl accessControl;
  private AuditLogService auditLogService;
  private final Clock fixedClock =
      Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

  private RemoveRoles useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(RoleRepository.class);
    RoleAssigner roleAssigner = mock();

    this.accessControl = mock(AccessControl.class);
    this.auditLogService = mock(AuditLogService.class);

    this.useCase =
        new RemoveRoles(
            this.repository,
            roleAssigner,
            this.accessControl,
            this.auditLogService,
            this.fixedClock);
  }

  @Test
  void testNoIdentifiers() {
    this.useCase.removeRoles(RemoveRolesRequest.builder().build());
    org.mockito.Mockito.verifyNoInteractions(
        this.repository, this.accessControl, this.auditLogService);
  }

  @Test
  void testAccessDenied() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.removeRoles(RemoveRolesRequest.builder().id("2").build()));
  }

  @Test
  void testRemoveWithId() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRoles(RemoveRolesRequest.builder().id("2").build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveWithAnchorNotFound() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.useCase.removeRoles(
                RemoveRolesRequest.builder().identifier(Identifier.ofAnchor("myanchor")).build()));
  }

  @Test
  void testRemoveWithAnchor() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRoles(
        RemoveRolesRequest.builder().identifier(Identifier.ofAnchor("myanchor")).build());

    verify(this.repository).remove("2");
  }

  @Test
  void testAudit() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRoles(RemoveRolesRequest.builder().id("2").build());

    CreateAuditLogRequest auditCommand =
        new CreateAuditLogRequest(
            AuditLogEntityType.ROLE.name(),
            role.id(),
            role.name(),
            AuditLogAction.REMOVE.name(),
            null,
            null,
            Instant.now(this.fixedClock),
            null);

    verify(this.auditLogService).createAuditLog(auditCommand);
  }
}
