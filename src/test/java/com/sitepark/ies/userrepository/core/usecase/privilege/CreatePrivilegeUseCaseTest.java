package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesRequest;
import com.sitepark.ies.userrepository.core.usecase.role.AssignPrivilegesToRolesUseCase;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreatePrivilegeUseCaseTest {
  private PrivilegeRepository privilegeRepository;
  private AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private CreatePrivilegeUseCase usecase;

  @BeforeEach
  void setUp() {
    this.privilegeRepository = mock();
    RoleRepository roleRepository = mock();
    this.assignPrivilegesToRolesUseCase = mock();
    this.privilegeAuthorizationService = mock();
    AuditLogService auditLogService = mock();
    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.usecase =
        new CreatePrivilegeUseCase(
            privilegeRepository,
            roleRepository,
            assignPrivilegesToRolesUseCase,
            privilegeAuthorizationService,
            auditLogService,
            fixedClock);
  }

  @Test
  void testWithId() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.usecase.createPrivilege(
                CreatePrivilegeRequest.builder()
                    .privilege(Privilege.builder().id("2").name("name").build())
                    .build()),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testWithoutPermission() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.usecase.createPrivilege(
                CreatePrivilegeRequest.builder()
                    .privilege(Privilege.builder().name("name").build())
                    .build()),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testPermissionNotCreatable() {

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.createPrivilege(
                CreatePrivilegeRequest.builder()
                    .privilege(
                        Privilege.builder().name("name").permission(new TestPermission()).build())
                    .build()),
        "Expected AccessDeniedException for privilege creation");
  }

  @Test
  void testRoleNotWritable() {

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.createPrivilege(
                CreatePrivilegeRequest.builder()
                    .privilege(
                        Privilege.builder().name("name").permission(new TestPermission()).build())
                    .roleIdentifiers(b -> b.id("1"))
                    .build()),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testWithExistsAnchor() {

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.privilegeRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    assertThrows(
        AnchorAlreadyExistsException.class,
        () ->
            this.usecase.createPrivilege(
                CreatePrivilegeRequest.builder()
                    .privilege(
                        Privilege.builder()
                            .name("name")
                            .anchor("anchor")
                            .permission(new TestPermission())
                            .build())
                    .build()),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreate() {

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.privilegeRepository.create(any())).thenReturn("456");

    String id =
        this.usecase.createPrivilege(
            CreatePrivilegeRequest.builder()
                .privilege(
                    Privilege.builder()
                        .name("name")
                        .anchor("anchor")
                        .permission(new TestPermission())
                        .build())
                .build());

    verify(this.privilegeRepository).create(any());
    assertEquals("456", id, "Expected ID to be returned after creation");
  }

  @Test
  void testWithAssignPrivilegesToRoles() {

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.privilegeRepository.create(any())).thenReturn("456");

    this.usecase.createPrivilege(
        CreatePrivilegeRequest.builder()
            .privilege(
                Privilege.builder()
                    .name("name")
                    .anchor("anchor")
                    .permission(new TestPermission())
                    .build())
            .roleIdentifiers(b -> b.id("1"))
            .build());

    verify(this.assignPrivilegesToRolesUseCase)
        .assignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .roleIdentifiers(b -> b.id("1"))
                .privilegeIdentifiers(b -> b.id("456"))
                .build());
  }
}
