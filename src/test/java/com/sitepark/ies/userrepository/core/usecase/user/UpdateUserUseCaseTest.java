package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateUserUseCaseTest {

  private AssignRolesToUsersUseCase assignRolesToUsersUseCase;
  private UserRepository repository;
  private AccessControl accessControl;
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
    this.assignRolesToUsersUseCase = mock(AssignRolesToUsersUseCase.class);
    this.repository = mock(UserRepository.class);
    this.extensionsNotifier = mock(ExtensionsNotifier.class);
    this.accessControl = mock(AccessControl.class);
    PatchServiceFactory patchServiceFactory = mock();
    this.patchService = mock();
    when(patchServiceFactory.createPatchService(User.class)).thenReturn(this.patchService);

    AuditLogService auditLogService = mock();
    this.fixedClock = Clock.fixed(changedAtAfter, ZoneOffset.UTC);

    this.useCase =
        new UpdateUserUseCase(
            this.assignRolesToUsersUseCase,
            this.repository,
            this.accessControl,
            this.extensionsNotifier,
            auditLogService,
            patchServiceFactory,
            this.fixedClock);
  }

  @Test
  void testAccessDeniedUpdate() {

    when(this.accessControl.isUserWritable()).thenReturn(false);

    User user = User.builder().id("123").login("test").build();

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testWithoutAnchorAndId() {

    when(this.accessControl.isUserWritable()).thenReturn(true);
    User user = User.builder().login("test").build();

    assertThrows(
        IllegalArgumentException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testUpdateUserNotFound() {

    when(this.accessControl.isUserWritable()).thenReturn(true);
    User user = User.builder().id("123").anchor("user.test").login("test").build();

    assertThrows(
        UserNotFoundException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  void testChangeLoginToAlreadyExistsLogin() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.resolveLogin("test2")).thenReturn(Optional.of("55"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));

    User user = User.builder().id("123").anchor("user.test").login("test2").build();

    assertThrows(
        LoginAlreadyExistsException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateUnchanged() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(patch.isEmpty()).thenReturn(true);
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    String id = this.useCase.updateUser(UpdateUserRequest.builder().user(user).build());
    assertEquals("123", id, "unexpected id");

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).resolveAnchor(any());
    verify(repository).get(any());

    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateRoles() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
    when(this.repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(this.repository.get(anyString())).thenReturn(Optional.of(storedUser));
    PatchDocument patch = mock();
    when(this.patchService.createPatch(any(), any())).thenReturn(patch);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    String id =
        this.useCase.updateUser(
            UpdateUserRequest.builder().user(user).roleIdentifiers(b -> b.id("333")).build());
    assertEquals("123", id, "unexpected id");

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).resolveAnchor(any());
    verify(repository).update(any());
    verify(this.assignRolesToUsersUseCase)
        .assignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.id("123"))
                .roleIdentifiers(b -> b.id("333"))
                .build());

    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateViaIdWithStoredAnchor() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
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

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).update(effectiveUser);

    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testViaAnchor() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
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

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).resolveAnchor(any());
    verify(repository).update(eq(effectiveUser));
    verify(extensionsNotifier).notifyUpdated(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateAnchor() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
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

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).resolveAnchor(any());
    verify(repository).update(eq(effectiveUser));
    verify(extensionsNotifier).notifyUpdated(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateLogin() {

    User storedUser = this.createStoredUser();

    when(this.accessControl.isUserWritable()).thenReturn(true);
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

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).update(eq(effectiveUser));
    verify(extensionsNotifier).notifyUpdated(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  void testUpdateAnchorNotFound() {

    when(this.accessControl.isUserWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(Anchor.ofString("user.test"))).thenReturn(Optional.empty());
    User user = User.builder().login("test").anchor("user.test2").build();

    assertThrows(
        AnchorNotFoundException.class,
        () -> this.useCase.updateUser(UpdateUserRequest.builder().user(user).build()));
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
