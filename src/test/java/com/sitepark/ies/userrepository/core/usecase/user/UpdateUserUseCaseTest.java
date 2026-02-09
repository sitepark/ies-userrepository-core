package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class UpdateUserUseCaseTest {

  private ReassignRolesToUsersUseCase reassignRolesToUsersUseCase;
  private UserRepository repository;
  private UserEntityAuthorizationService userEntityAuthorizationService;
  private ExtensionsNotifier extensionsNotifier;
  private PatchService<User> patchService;
  private Clock fixedClock;
  private UpdateUserUseCase useCase;

  private final Instant createdAt =
      LocalDateTime.of(2024, 6, 10, 14, 30).atZone(ZoneOffset.UTC).toInstant();

  private final Instant changedAtBefore =
      LocalDateTime.of(2024, 6, 10, 16, 30).atZone(ZoneOffset.UTC).toInstant();

  private final Instant changedAtAfter =
      LocalDateTime.of(2024, 6, 13, 12, 30).atZone(ZoneOffset.UTC).toInstant();

  @BeforeEach
  void setUp() {
    this.reassignRolesToUsersUseCase = mock(ReassignRolesToUsersUseCase.class);
    this.repository = mock(UserRepository.class);
    this.extensionsNotifier = mock(ExtensionsNotifier.class);
    this.userEntityAuthorizationService = mock(UserEntityAuthorizationService.class);
    PatchServiceFactory patchServiceFactory = mock();
    this.patchService = mock();
    when(patchServiceFactory.createPatchService(User.class)).thenReturn(this.patchService);

    this.fixedClock = Clock.fixed(changedAtAfter, ZoneOffset.UTC);

    this.useCase =
        new UpdateUserUseCase(
            this.reassignRolesToUsersUseCase,
            this.repository,
            this.userEntityAuthorizationService,
            this.extensionsNotifier,
            patchServiceFactory,
            this.fixedClock);
  }

  @Test
  void testAccessDeniedUpdate() {

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(false);

    User user = User.builder().id("123").login("test").build();

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testWithoutAnchorAndId() {

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    User user = User.builder().login("test").build();

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testUpdateUserNotFound() {

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    User user = User.builder().id("123").anchor("user.test").login("test").build();

    assertThrows(
        UserNotFoundException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testChangeLoginToAlreadyExistsLogin() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.resolveLogin("test2")).thenReturn(Optional.of("55"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").anchor("user.test").login("test2").build();

    assertThrows(
        LoginAlreadyExistsException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testUpdateUnchanged() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals("123", result.userId(), "Should return correct user ID");
  }

  @Test
  void testUpdateUnchangedHasNoUserChanges() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals(false, result.hasUserChanges(), "Should have no user changes when patch is empty");
  }

  @Test
  void testUpdateUnchangedHasNoRoleChanges() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals(
        false, result.hasRoleChanges(), "Should have no role changes when no roles provided");
  }

  @Test
  void testUpdateUnchangedDoesNotCallRepository() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    verify(repository).get(anyString());
  }

  @Test
  void testUpdateUnchangedDoesNotCallUpdate() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    verify(repository).resolveAnchor(any());
  }

  @Test
  void testUpdateUnchangedDoesNotCallRepositoryUpdate() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    verify(repository, Mockito.never()).update(any());
  }

  @Test
  void testUpdateRolesReturnsCorrectUserId() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    // Mock role assignment to return a Result
    when(this.reassignRolesToUsersUseCase.reassignRolesToUsers(any()))
        .thenReturn(ReassignRolesToUsersResult.skipped());

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    UpdateUserResult result =
        this.useCase.updateUser(
            UpdateUserRequest.builder().user(user).roleIdentifiers(b -> b.id("333")).build());

    assertEquals("123", result.userId(), "Should return correct user ID");
  }

  @Test
  void testUpdateRolesCallsAssignRoles() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    // Mock role assignment to return a Result
    when(this.reassignRolesToUsersUseCase.reassignRolesToUsers(any()))
        .thenReturn(ReassignRolesToUsersResult.skipped());

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    this.useCase.updateUser(
        UpdateUserRequest.builder().user(user).roleIdentifiers(b -> b.id("333")).build());

    verify(this.reassignRolesToUsersUseCase)
        .reassignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.id("123"))
                .roleIdentifiers(b -> b.id("333"))
                .build());
  }

  @Test
  void testUpdateRolesCallsUpdate() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    // Mock role assignment to return a Result
    when(this.reassignRolesToUsersUseCase.reassignRolesToUsers(any()))
        .thenReturn(ReassignRolesToUsersResult.skipped());

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    this.useCase.updateUser(
        UpdateUserRequest.builder().user(user).roleIdentifiers(b -> b.id("333")).build());

    verify(repository).update(any());
  }

  @Test
  void testUpdateViaIdWithStoredAnchorReturnsCorrectUserId() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").login("test").lastName("B").build();
    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals("123", result.userId(), "Should return correct user ID");
  }

  @Test
  void testUpdateViaIdWithStoredAnchorUpdatesUser() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").login("test").lastName("B").build();
    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    User effectiveUser =
        User.builder()
            .id("123")
            .login("test")
            .anchor("user.test")
            .lastName("B")
            .createdAt(this.createdAt)
            .changedAt(this.changedAtAfter)
            .build();

    verify(repository).update(effectiveUser);
  }

  @Test
  void testViaAnchorReturnsCorrectUserId() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.resolveAnchor(Anchor.ofString("user.test")))
        .thenReturn(Optional.of("123"));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(false);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().login("test").anchor("user.test").lastName("B").build();

    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals("123", result.userId(), "Should return correct user ID");
  }

  @Test
  void testViaAnchorUpdatesUser() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.resolveAnchor(Anchor.ofString("user.test")))
        .thenReturn(Optional.of("123"));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(false);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().login("test").anchor("user.test").lastName("B").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    User effectiveUser =
        User.builder()
            .id("123")
            .login("test")
            .anchor("user.test")
            .lastName("B")
            .createdAt(this.createdAt)
            .changedAt(this.fixedClock.instant())
            .build();

    verify(repository).update(eq(effectiveUser));
  }

  @Test
  void testViaAnchorNotifiesExtensions() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.resolveAnchor(Anchor.ofString("user.test")))
        .thenReturn(Optional.of("123"));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(false);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().login("test").anchor("user.test").lastName("B").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    verify(extensionsNotifier).notifyUpdated(any());
  }

  @Test
  void testUpdateAnchorReturnsCorrectUserId() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").login("test").anchor("user.test2").build();

    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals("123", result.userId(), "Should return correct user ID");
  }

  @Test
  void testUpdateAnchorUpdatesUser() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").login("test").anchor("user.test2").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    User effectiveUser =
        User.builder()
            .id("123")
            .login("test")
            .anchor("user.test2")
            .createdAt(this.createdAt)
            .changedAt(this.fixedClock.instant())
            .build();

    verify(repository).update(eq(effectiveUser));
  }

  @Test
  void testUpdateAnchorNotifiesExtensions() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").login("test").anchor("user.test2").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    verify(extensionsNotifier).notifyUpdated(any());
  }

  @Test
  void testUpdateLoginReturnsCorrectUserId() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("55"));
    when(this.repository.resolveLogin("test2")).thenReturn(Optional.empty());
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").login("test2").build();

    UpdateUserResult result =
        this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    assertEquals("123", result.userId(), "Should return correct user ID");
  }

  @Test
  void testUpdateLoginUpdatesUser() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("55"));
    when(this.repository.resolveLogin("test2")).thenReturn(Optional.empty());
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").login("test2").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    User effectiveUser =
        User.builder()
            .id("123")
            .anchor("user.test")
            .login("test2")
            .createdAt(this.createdAt)
            .changedAt(this.changedAtAfter)
            .build();

    verify(repository).update(eq(effectiveUser));
  }

  @Test
  void testUpdateLoginNotifiesExtensions() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("55"));
    when(this.repository.resolveLogin("test2")).thenReturn(Optional.empty());
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").login("test2").build();

    this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());

    verify(extensionsNotifier).notifyUpdated(any());
  }

  @Test
  void testUpdateAnchorNotFound() {

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveAnchor(Anchor.ofString("user.test"))).thenReturn(Optional.empty());
    User user = User.builder().login("test").anchor("user.test2").build();

    assertThrows(
        AnchorNotFoundException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testUserUnchangedWithRoleChangesHasNoUserChanges() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.reassignRolesToUsersUseCase.reassignRolesToUsers(any()))
        .thenReturn(ReassignRolesToUsersResult.skipped());

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    UpdateUserResult result =
        this.useCase.updateUser(
            UpdateUserRequest.builder().user(user).roleIdentifiers(b -> b.id("333")).build());

    assertEquals(false, result.hasUserChanges(), "Should have no user changes when patch is empty");
  }

  @Test
  void testUserUnchangedWithRoleChangesCallsAssignRoles() {

    User storedUser = this.createStoredUser();

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);
    when(this.reassignRolesToUsersUseCase.reassignRolesToUsers(any()))
        .thenReturn(ReassignRolesToUsersResult.skipped());

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    this.useCase.updateUser(
        UpdateUserRequest.builder().user(user).roleIdentifiers(b -> b.id("333")).build());

    verify(this.reassignRolesToUsersUseCase)
        .reassignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.id("123"))
                .roleIdentifiers(b -> b.id("333"))
                .build());
  }

  private User createStoredUser() {
    return User.builder()
        .id("123")
        .login("test")
        .anchor("user.test")
        .lastName("A")
        .createdAt(this.createdAt)
        .changedAt(this.changedAtBefore)
        .build();
  }
}
