package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserManagementPermission;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestorePrivilegeUseCaseTest {

  private PrivilegeRepository repository;
  private RoleAssigner roleAssigner;
  private PrivilegeEntityAuthorizationService privilegeAuthorizationService;
  private Clock fixedClock;

  private RestorePrivilegeUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.roleAssigner = mock();
    this.privilegeAuthorizationService = mock();
    this.fixedClock = Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

    this.useCase =
        new RestorePrivilegeUseCase(
            this.repository,
            this.roleAssigner,
            this.privilegeAuthorizationService,
            this.fixedClock);
  }

  @Test
  void testMissingPrivilegeIdThrowsException() {
    Privilege privilege =
        Privilege.builder().name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot)),
        "Should throw IllegalArgumentException when privilege ID is null");
  }

  @Test
  void testBlankPrivilegeIdThrowsException() {
    Privilege privilege =
        Privilege.builder().id("").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot)),
        "Should throw IllegalArgumentException when privilege ID is blank");
  }

  @Test
  void testMissingPermissionThrowsException() {
    Privilege privilege = Privilege.builder().id("2").name("test").build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot)),
        "Should throw IllegalArgumentException when permission is null");
  }

  @Test
  void testNotCreatableThrowsAccessDenied() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot)),
        "Should throw AccessDeniedException when privilege is not creatable");
  }

  @Test
  void testPrivilegeAlreadyExistsReturnsSkipped() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(privilege));

    RestorePrivilegeResult result =
        this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    assertInstanceOf(
        RestorePrivilegeResult.Skipped.class,
        result,
        "Should return Skipped result when privilege already exists");
  }

  @Test
  void testAnchorAlreadyExistsThrowsException() {
    Privilege privilege =
        Privilege.builder()
            .id("2")
            .name("test")
            .permission(UserManagementPermission.EMPTY)
            .anchor("myanchor")
            .build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());
    when(this.repository.resolveAnchor(Anchor.ofString("myanchor")))
        .thenReturn(Optional.of("existing"));

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot)),
        "Should throw AnchorAlreadyExistsException when anchor already exists");
  }

  @Test
  void testRestoreCallsRepository() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    verify(this.repository).restore(privilege);
  }

  @Test
  void testRestoreWithoutRolesDoesNotCallRoleAssigner() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    verify(this.roleAssigner, never()).assignPrivilegesToRoles(anyList(), anyList());
  }

  @Test
  void testRestoreWithRolesCallsRoleAssigner() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of("role1", "role2"));

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("role1", "role2"), List.of("2"));
  }

  @Test
  void testRestoreReturnsRestoredResult() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestorePrivilegeResult result =
        this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    assertInstanceOf(
        RestorePrivilegeResult.Restored.class, result, "Should return Restored result on success");
  }

  @Test
  void testRestoredResultContainsPrivilegeId() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestorePrivilegeResult result =
        this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    RestorePrivilegeResult.Restored restored = (RestorePrivilegeResult.Restored) result;
    assertEquals("2", restored.privilegeId(), "Restored result should contain the privilege ID");
  }

  @Test
  void testRestoredResultContainsSnapshot() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of("role1"));

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestorePrivilegeResult result =
        this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    RestorePrivilegeResult.Restored restored = (RestorePrivilegeResult.Restored) result;
    assertEquals(snapshot, restored.snapshot(), "Restored result should contain the snapshot");
  }

  @Test
  void testRestoredResultContainsTimestamp() {
    Privilege privilege =
        Privilege.builder().id("2").name("test").permission(UserManagementPermission.EMPTY).build();
    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, List.of());

    when(this.privilegeAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestorePrivilegeResult result =
        this.useCase.restorePrivilege(new RestorePrivilegeRequest(snapshot));

    RestorePrivilegeResult.Restored restored = (RestorePrivilegeResult.Restored) result;
    assertEquals(
        Instant.now(this.fixedClock),
        restored.timestamp(),
        "Restored result should contain timestamp from fixed clock");
  }
}
