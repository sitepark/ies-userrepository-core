package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateRoleUseCaseTest {

  private AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private RoleRepository repository;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;
  private PatchService<Role> patchService;

  private UpdateRoleUseCase useCase;

  @BeforeEach
  void setUp() {
    this.assignPrivilegesToRolesUseCase = mock();
    this.repository = mock();
    this.roleEntityAuthorizationService = mock();
    PatchServiceFactory patchServiceFactory = mock();
    this.patchService = mock();
    when(patchServiceFactory.createPatchService(Role.class)).thenReturn(this.patchService);

    AuditLogService auditLogService = mock();
    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.useCase =
        new UpdateRoleUseCase(
            this.assignPrivilegesToRolesUseCase,
            this.repository,
            this.roleEntityAuthorizationService,
            auditLogService,
            patchServiceFactory,
            fixedClock);
  }

  @Test
  void testAccessDenied() {

    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(false);

    Role role = Role.builder().id("1").name("test").build();

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.updateRole(UpdateRoleRequest.builder().role(role).build()));
  }

  @Test
  void testNoIdNoAnchor() {
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    Role role = Role.builder().name("test").build();

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.updateRole(UpdateRoleRequest.builder().role(role).build()));
  }

  @Test
  void testAnchorNotFound() {

    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    Role role = Role.builder().anchor("test").name("test").build();

    assertThrows(
        AnchorNotFoundException.class,
        () -> this.useCase.updateRole(UpdateRoleRequest.builder().role(role).build()));
  }

  @Test
  void testAnchorAlreadyExists() {
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    Role role = Role.builder().id("1").anchor("test").name("test").build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.updateRole(UpdateRoleRequest.builder().role(role).build()));
  }

  @Test
  void testUpdateWithAnchor() {
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.repository.get(any()))
        .thenReturn(Optional.of(Role.builder().anchor("test").name("test1").build()));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    Role role = Role.builder().anchor("test").name("test2").build();
    this.useCase.updateRole(UpdateRoleRequest.builder().role(role).build());

    Role expected = role.toBuilder().id("1").build();
    verify(this.repository).update(expected);
  }

  @Test
  void testUpdateReturnId() {
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.repository.get(any()))
        .thenReturn(Optional.of(Role.builder().anchor("test").name("test1").build()));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    Role role = Role.builder().anchor("test").name("test2").build();
    String id = this.useCase.updateRole(UpdateRoleRequest.builder().role(role).build());
    assertEquals("1", id, "id should be resolved from anchor");
  }

  @Test
  void testUpdateWithPrivilegeIds() {
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.repository.get(any()))
        .thenReturn(Optional.of(Role.builder().anchor("test").name("test1").build()));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    Role role = Role.builder().id("1").name("test2").build();
    this.useCase.updateRole(
        UpdateRoleRequest.builder().role(role).privilegeIdentifiers(b -> b.id("12")).build());

    verify(this.assignPrivilegesToRolesUseCase)
        .assignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .roleIdentifiers(b -> b.id("1"))
                .privilegeIdentifiers(b -> b.id("12"))
                .build());
  }
}
