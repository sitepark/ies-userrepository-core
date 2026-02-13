package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestoreRoleUseCaseTest {

  private RoleRepository repository;
  private RoleAssigner roleAssigner;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;
  private Clock fixedClock;

  private RestoreRoleUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.roleAssigner = mock();
    this.roleEntityAuthorizationService = mock();
    this.fixedClock = Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

    this.useCase =
        new RestoreRoleUseCase(
            this.repository,
            this.roleAssigner,
            this.roleEntityAuthorizationService,
            this.fixedClock);
  }

  @Test
  void testMissingRoleIdThrowsException() {
    Role role = Role.builder().name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restoreRole(new RestoreRoleRequest(snapshot)),
        "Should throw IllegalArgumentException when role ID is null");
  }

  @Test
  void testBlankRoleIdThrowsException() {
    Role role = Role.builder().id("").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restoreRole(new RestoreRoleRequest(snapshot)),
        "Should throw IllegalArgumentException when role ID is blank");
  }

  @Test
  void testNotCreatableThrowsAccessDenied() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.restoreRole(new RestoreRoleRequest(snapshot)),
        "Should throw AccessDeniedException when role is not creatable");
  }

  @Test
  void testNotWritableWithUsersThrowsAccessDenied() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of("user1"), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(false);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.restoreRole(new RestoreRoleRequest(snapshot)),
        "Should throw AccessDeniedException when role is not writable with users");
  }

  @Test
  void testRoleAlreadyExistsReturnsSkipped() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(role));

    RestoreRoleResult result = this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    assertInstanceOf(
        RestoreRoleResult.Skipped.class,
        result,
        "Should return Skipped result when role already exists");
  }

  @Test
  void testAnchorAlreadyExistsThrowsException() {
    Role role = Role.builder().id("2").name("test").anchor("myanchor").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());
    when(this.repository.resolveAnchor(Anchor.ofString("myanchor")))
        .thenReturn(Optional.of("existing"));

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.restoreRole(new RestoreRoleRequest(snapshot)),
        "Should throw AnchorAlreadyExistsException when anchor already exists");
  }

  @Test
  void testRestoreCallsRepository() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    verify(this.repository).restore(role);
  }

  @Test
  void testRestoreWithoutUsersDoesNotCallRoleAssignerForUsers() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    verify(this.roleAssigner, never()).assignRolesToUsers(anyList(), anyList());
  }

  @Test
  void testRestoreWithUsersCallsRoleAssignerForUsers() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of("user1", "user2"), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    verify(this.roleAssigner).assignRolesToUsers(List.of("user1", "user2"), List.of("2"));
  }

  @Test
  void testRestoreWithoutPrivilegesDoesNotCallRoleAssignerForPrivileges() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    verify(this.roleAssigner, never()).assignPrivilegesToRoles(anyList(), anyList());
  }

  @Test
  void testRestoreWithPrivilegesCallsRoleAssignerForPrivileges() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of("priv1", "priv2"));

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("2"), List.of("priv1", "priv2"));
  }

  @Test
  void testRestoreReturnsRestoredResult() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreRoleResult result = this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    assertInstanceOf(
        RestoreRoleResult.Restored.class, result, "Should return Restored result on success");
  }

  @Test
  void testRestoredResultContainsRoleId() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreRoleResult result = this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    RestoreRoleResult.Restored restored = (RestoreRoleResult.Restored) result;
    assertEquals("2", restored.roleId(), "Restored result should contain the role ID");
  }

  @Test
  void testRestoredResultContainsSnapshot() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of("user1"), List.of("priv1"));

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.roleEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreRoleResult result = this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    RestoreRoleResult.Restored restored = (RestoreRoleResult.Restored) result;
    assertEquals(snapshot, restored.snapshot(), "Restored result should contain the snapshot");
  }

  @Test
  void testRestoredResultContainsTimestamp() {
    Role role = Role.builder().id("2").name("test").build();
    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), List.of());

    when(this.roleEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreRoleResult result = this.useCase.restoreRole(new RestoreRoleRequest(snapshot));

    RestoreRoleResult.Restored restored = (RestoreRoleResult.Restored) result;
    assertEquals(
        Instant.now(this.fixedClock),
        restored.timestamp(),
        "Restored result should contain timestamp from fixed clock");
  }
}
