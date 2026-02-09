package com.sitepark.ies.userrepository.core.usecase.privilege;

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
import com.sitepark.ies.sharedkernel.security.Permission;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesRequest;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesUseCase;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePrivilegeUseCaseTest {
  private AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private PrivilegeRepository repository;
  private PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private PatchService<Privilege> patchService;

  private UpdatePrivilegeUseCase usecase;

  @BeforeEach
  void setUp() {
    this.assignPrivilegesToRolesUseCase = mock();
    this.repository = mock();
    this.privilegeAuthorizationService = mock();
    PatchServiceFactory patchServiceFactory = mock();
    this.patchService = mock();

    when(patchServiceFactory.createPatchService(Privilege.class)).thenReturn(this.patchService);

    AuditLogService auditLogService = mock();
    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.usecase =
        new UpdatePrivilegeUseCase(
            this.assignPrivilegesToRolesUseCase,
            this.repository,
            this.privilegeAuthorizationService,
            auditLogService,
            patchServiceFactory,
            fixedClock);
  }

  @Test
  void testMissingPermission() {
    Privilege privilege = Privilege.builder().id("1").name("testPrivilege").build();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.usecase.updatePrivilege(
                UpdatePrivilegeRequest.builder().privilege(privilege).build()),
        "Expected IllegalArgumentException for privilege without permissions");
  }

  @Test
  void testAssesDeniedForPrivilege() {
    when(this.privilegeAuthorizationService.isWritable(anyString())).thenReturn(false);
    Privilege privilege =
        Privilege.builder().id("1").name("testPrivilege").permission(new TestPermission()).build();
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.updatePrivilege(
                UpdatePrivilegeRequest.builder().privilege(privilege).build()),
        "Expected AccessDeniedException for updating privilege without permission");
  }

  @Test
  void testAnchorNotFound() {
    when(this.privilegeAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.empty());
    Privilege privilege =
        Privilege.builder()
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new TestPermission())
            .build();
    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.updatePrivilege(
                UpdatePrivilegeRequest.builder().privilege(privilege).build()),
        "Expected AnchorNotFoundException for non-existing privilege");
  }

  @Test
  void testExistsAnchor() {
    when(this.privilegeAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));
    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new TestPermission())
            .build();
    assertThrows(
        AnchorAlreadyExistsException.class,
        () ->
            this.usecase.updatePrivilege(
                UpdatePrivilegeRequest.builder().privilege(privilege).build()),
        "Expected AnchorAlreadyExistsException for existing anchor");
  }

  @Test
  void testIdAndAnchorMissing() {
    when(this.privilegeAuthorizationService.isWritable(anyString())).thenReturn(true);
    Privilege privilege =
        Privilege.builder().name("testPrivilege").permission(new TestPermission()).build();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.usecase.updatePrivilege(
                UpdatePrivilegeRequest.builder().privilege(privilege).build()),
        "Expected IllegalArgumentException for privilege without ID or anchor");
  }

  @Test
  void testUpdate() {
    when(this.privilegeAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    Permission a = new TestPermission();
    Permission b = new TestPermission();

    Privilege oldPrivilege =
        Privilege.builder().anchor("anchor").name("testPrivilegeOld").permission(a).build();
    when(this.repository.get(any())).thenReturn(Optional.of(oldPrivilege));

    Privilege newPrivilege =
        Privilege.builder().anchor("anchor").name("testPrivilegeNew").permission(b).build();
    this.usecase.updatePrivilege(UpdatePrivilegeRequest.builder().privilege(newPrivilege).build());

    Privilege expected =
        Privilege.builder().id("2").anchor("anchor").name("testPrivilegeNew").permission(b).build();

    verify(this.repository).update(expected);
  }

  @Test
  void testAssignPrivilegesToRoles() {
    when(this.privilegeAuthorizationService.isWritable(anyString())).thenReturn(true);

    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    Privilege oldPrivilege =
        Privilege.builder()
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new TestPermission())
            .build();
    when(this.repository.get(any())).thenReturn(Optional.of(oldPrivilege));

    Privilege newPrivilege =
        Privilege.builder().id("3").name("testPrivilege").permission(new TestPermission()).build();
    this.usecase.updatePrivilege(
        UpdatePrivilegeRequest.builder()
            .privilege(newPrivilege)
            .roleIdentifiers(b -> b.ids("1", "2"))
            .build());

    verify(this.assignPrivilegesToRolesUseCase)
        .assignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .roleIdentifiers(b -> b.ids("1", "2"))
                .privilegeIdentifiers(b -> b.id("3"))
                .build());
  }
}
