package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveRolesUseCaseTest {

  private RoleRepository repository;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;
  private AuditLogService auditLogService;
  private final Clock fixedClock =
      Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

  private RemoveRolesUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    RoleAssigner roleAssigner = mock();

    this.roleEntityAuthorizationService = mock();
    this.auditLogService = mock();

    this.useCase =
        new RemoveRolesUseCase(
            this.repository,
            roleAssigner,
            this.roleEntityAuthorizationService,
            this.auditLogService,
            this.fixedClock);
  }

  @Test
  void testNoIdentifiers() {
    this.useCase.removeRoles(RemoveRolesRequest.builder().build());
    org.mockito.Mockito.verifyNoInteractions(
        this.repository, this.roleEntityAuthorizationService, this.auditLogService);
  }

  @Test
  void testAccessDenied() {
    when(this.roleEntityAuthorizationService.isRemovable(anyList())).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.removeRoles(
                RemoveRolesRequest.builder().identifiers(b -> b.id("2")).build()));
  }

  @Test
  void testRemoveWithId() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(anyList())).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRoles(RemoveRolesRequest.builder().identifiers(b -> b.id("2")).build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveWithAnchorNotFound() {
    when(this.roleEntityAuthorizationService.isRemovable(anyList())).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.useCase.removeRoles(
                RemoveRolesRequest.builder().identifiers(b -> b.add("myanchor")).build()));
  }

  @Test
  void testRemoveWithAnchor() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(anyList())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRoles(
        RemoveRolesRequest.builder().identifiers(b -> b.add("myanchor")).build());

    verify(this.repository).remove("2");
  }

  @Test
  void testAudit() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(anyList())).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRoles(RemoveRolesRequest.builder().identifiers(b -> b.id("2")).build());

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
