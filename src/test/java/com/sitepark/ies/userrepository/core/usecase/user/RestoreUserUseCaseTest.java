package com.sitepark.ies.userrepository.core.usecase.user;

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
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserSnapshot;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestoreUserUseCaseTest {

  private UserRepository repository;
  private RoleAssigner roleAssigner;
  private UserEntityAuthorizationService userEntityAuthorizationService;
  private Clock fixedClock;

  private RestoreUserUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.roleAssigner = mock();
    this.userEntityAuthorizationService = mock();
    this.fixedClock = Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

    this.useCase =
        new RestoreUserUseCase(
            this.repository,
            this.roleAssigner,
            this.userEntityAuthorizationService,
            this.fixedClock);
  }

  @Test
  void testMissingUserIdThrowsException() {
    User user = User.builder().login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restoreUser(new RestoreUserRequest(snapshot)),
        "Should throw IllegalArgumentException when user ID is null");
  }

  @Test
  void testBlankUserIdThrowsException() {
    User user = User.builder().id("").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.restoreUser(new RestoreUserRequest(snapshot)),
        "Should throw IllegalArgumentException when user ID is blank");
  }

  @Test
  void testNotCreatableThrowsAccessDenied() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.restoreUser(new RestoreUserRequest(snapshot)),
        "Should throw AccessDeniedException when user is not creatable");
  }

  @Test
  void testUserAlreadyExistsReturnsSkipped() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.of(user));

    RestoreUserResult result = this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    assertInstanceOf(
        RestoreUserResult.Skipped.class,
        result,
        "Should return Skipped result when user already exists");
  }

  @Test
  void testAnchorAlreadyExistsThrowsException() {
    User user = User.builder().id("2").login("test").lastName("test").anchor("myanchor").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());
    when(this.repository.resolveAnchor(Anchor.ofString("myanchor")))
        .thenReturn(Optional.of("existing"));

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.restoreUser(new RestoreUserRequest(snapshot)),
        "Should throw AnchorAlreadyExistsException when anchor already exists");
  }

  @Test
  void testRestoreCallsRepository() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    verify(this.repository).restore(user);
  }

  @Test
  void testRestoreWithoutRolesDoesNotCallRoleAssigner() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    verify(this.roleAssigner, never()).assignRolesToUsers(anyList(), anyList());
  }

  @Test
  void testRestoreWithRolesCallsRoleAssigner() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of("role1", "role2"));

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    verify(this.roleAssigner).assignRolesToUsers(List.of("2"), List.of("role1", "role2"));
  }

  @Test
  void testRestoreReturnsRestoredResult() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreUserResult result = this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    assertInstanceOf(
        RestoreUserResult.Restored.class, result, "Should return Restored result on success");
  }

  @Test
  void testRestoredResultContainsUserId() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreUserResult result = this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    RestoreUserResult.Restored restored = (RestoreUserResult.Restored) result;
    assertEquals("2", restored.userId(), "Restored result should contain the user ID");
  }

  @Test
  void testRestoredResultContainsSnapshot() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of("role1"));

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreUserResult result = this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    RestoreUserResult.Restored restored = (RestoreUserResult.Restored) result;
    assertEquals(snapshot, restored.snapshot(), "Restored result should contain the snapshot");
  }

  @Test
  void testRestoredResultContainsTimestamp() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    UserSnapshot snapshot = new UserSnapshot(user, List.of());

    when(this.userEntityAuthorizationService.isCreatable()).thenReturn(true);
    when(this.repository.get("2")).thenReturn(Optional.empty());

    RestoreUserResult result = this.useCase.restoreUser(new RestoreUserRequest(snapshot));

    RestoreUserResult.Restored restored = (RestoreUserResult.Restored) result;
    assertEquals(
        Instant.now(this.fixedClock),
        restored.timestamp(),
        "Restored result should contain timestamp from fixed clock");
  }
}
