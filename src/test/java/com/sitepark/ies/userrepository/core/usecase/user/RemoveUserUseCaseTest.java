package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveUserUseCaseTest {

  private UserRepository repository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;
  private Clock fixedClock;

  private RemoveUserUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.roleAssigner = mock();
    this.accessControl = mock();
    this.fixedClock = Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

    this.useCase =
        new RemoveUserUseCase(
            this.repository, this.roleAssigner, this.accessControl, this.fixedClock);
  }

  @Test
  void testAccessDenied() {
    when(this.accessControl.isUserRemovable()).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.removeUser(RemoveUserRequest.builder().id("2").build()),
        "Should throw AccessDeniedException when user removal is not allowed");
  }

  @Test
  void testRemoveWithIdCallsRepositoryRemove() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of());

    this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveWithIdReturnsRemovedResult() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of());

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    assertInstanceOf(
        RemoveUserResult.Removed.class,
        result,
        "Should return Removed result when user is successfully removed");
  }

  @Test
  void testRemoveWithAnchorNotFound() {
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.empty());

    assertThrows(
        AnchorNotFoundException.class,
        () -> this.useCase.removeUser(RemoveUserRequest.builder().anchor("myanchor").build()),
        "Should throw AnchorNotFoundException when anchor does not exist");
  }

  @Test
  void testRemoveWithAnchorCallsRepositoryRemove() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of());

    this.useCase.removeUser(RemoveUserRequest.builder().anchor("myanchor").build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveWithAnchorReturnsRemovedResult() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of());

    RemoveUserResult result =
        this.useCase.removeUser(RemoveUserRequest.builder().anchor("myanchor").build());

    assertInstanceOf(
        RemoveUserResult.Removed.class,
        result,
        "Should return Removed result when user found via anchor is successfully removed");
  }

  @Test
  void testUserNotFound() {
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("999")).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> this.useCase.removeUser(RemoveUserRequest.builder().id("999").build()),
        "Should throw UserNotFoundException when user does not exist");
  }

  @Test
  void testBuiltInAdministratorIsSkipped() {
    when(this.accessControl.isUserRemovable()).thenReturn(true);

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("1").build());

    assertInstanceOf(
        RemoveUserResult.Skipped.class,
        result,
        "Should return Skipped result for built-in administrator");
  }

  @Test
  void testBuiltInAdministratorSkippedResultContainsUserId() {
    when(this.accessControl.isUserRemovable()).thenReturn(true);

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("1").build());

    RemoveUserResult.Skipped skipped = (RemoveUserResult.Skipped) result;
    assertEquals("1", skipped.userId(), "Skipped result should contain administrator user ID");
  }

  @Test
  void testBuiltInAdministratorSkippedResultContainsReason() {
    when(this.accessControl.isUserRemovable()).thenReturn(true);

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("1").build());

    RemoveUserResult.Skipped skipped = (RemoveUserResult.Skipped) result;
    assertEquals(
        "Built-in administrator cannot be removed",
        skipped.reason(),
        "Skipped result should contain correct reason");
  }

  @Test
  void testRemovedResultContainsUserId() {
    User user = User.builder().id("2").login("test").lastName("User").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of("role1", "role2"));

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    RemoveUserResult.Removed removed = (RemoveUserResult.Removed) result;
    assertEquals("2", removed.userId(), "Removed result should contain correct user ID");
  }

  @Test
  void testRemovedResultContainsDisplayName() {
    User user = User.builder().id("2").login("test").lastName("User").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of("role1", "role2"));

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    RemoveUserResult.Removed removed = (RemoveUserResult.Removed) result;
    assertEquals("User", removed.displayName(), "Removed result should contain display name");
  }

  @Test
  void testRemovedResultContainsUserSnapshot() {
    User user = User.builder().id("2").login("test").lastName("User").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of("role1", "role2"));

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    RemoveUserResult.Removed removed = (RemoveUserResult.Removed) result;
    assertEquals(
        user, removed.snapshot().user(), "Snapshot should contain the user that was removed");
  }

  @Test
  void testRemovedResultSnapshotContainsRoleIds() {
    User user = User.builder().id("2").login("test").lastName("User").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of("role1", "role2"));

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    RemoveUserResult.Removed removed = (RemoveUserResult.Removed) result;
    assertEquals(
        List.of("role1", "role2"),
        removed.snapshot().roleIds(),
        "Snapshot should contain the user's role IDs");
  }

  @Test
  void testRemovedResultContainsTimestamp() {
    User user = User.builder().id("2").login("test").lastName("User").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));
    when(this.roleAssigner.getRolesAssignByUser("2")).thenReturn(List.of("role1", "role2"));

    RemoveUserResult result = this.useCase.removeUser(RemoveUserRequest.builder().id("2").build());

    RemoveUserResult.Removed removed = (RemoveUserResult.Removed) result;
    assertEquals(
        Instant.now(this.fixedClock),
        removed.timestamp(),
        "Removed result should contain timestamp from fixed clock");
  }
}
