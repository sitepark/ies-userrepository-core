package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.PasswordHasher;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateUserUseCaseTest {

  private UserRepository userRepository;
  private AssignRolesToUsersUseCase assignRolesToUsersUseCase;
  private AccessControl accessControl;
  private PasswordHasher passwordHasher;
  private CreateUserUseCase userCase;

  @BeforeEach
  void setUp() {
    this.userRepository = mock();
    RoleRepository roleRepository = mock();
    this.assignRolesToUsersUseCase = mock();
    this.accessControl = mock();
    ExtensionsNotifier extensionsNotifier = mock();
    this.passwordHasher = mock();

    AuditLogService auditLogService = mock();
    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.userCase =
        new CreateUserUseCase(
            this.userRepository,
            roleRepository,
            this.assignRolesToUsersUseCase,
            this.accessControl,
            extensionsNotifier,
            this.passwordHasher,
            auditLogService,
            fixedClock);
  }

  @Test
  void testAccessDeniedCreate() {

    when(this.accessControl.isUserCreatable()).thenReturn(false);

    User user = User.builder().login("test").lastName("Test").build();

    assertThrows(
        AccessDeniedException.class,
        () -> this.userCase.createUser(CreateUserRequest.builder().user(user).build()));
  }

  @Test
  void testWithId() {

    User user = User.builder().id("123").login("test").build();

    assertThrows(
        IllegalArgumentException.class,
        () -> this.userCase.createUser(CreateUserRequest.builder().user(user).build()));
  }

  @Test
  void testAnchorAlreadyExists() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.userRepository.resolveAnchor(Anchor.ofString("test.user")))
        .thenReturn(Optional.of("123"));

    User user = User.builder().anchor("test.user").login("test").lastName("Test").build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.userCase.createUser(CreateUserRequest.builder().user(user).build()));
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreateWithRoles() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.userRepository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(this.userRepository.create(any())).thenReturn("123");

    User user = User.builder().login("test").lastName("Test").build();

    String id =
        this.userCase.createUser(
            CreateUserRequest.builder().user(user).roleIdentifiers(r -> r.id("333")).build());

    assertEquals("123", id, "unexpected id");

    verify(this.userRepository).create(eq(user));
    verify(this.assignRolesToUsersUseCase)
        .assignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.id("123"))
                .roleIdentifiers(b -> b.id("333"))
                .build());
  }

  @Test
  void testCreateWithAnchor() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.userRepository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(this.userRepository.create(any())).thenReturn("123");

    User user = User.builder().anchor("test.anchor").login("test").lastName("Test").build();

    this.userCase.createUser(CreateUserRequest.builder().user(user).build());

    verify(userRepository).create(eq(user));
  }

  @Test
  void testCreateWithPassword() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.userRepository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(this.userRepository.create(any())).thenReturn("123");

    User user =
        User.builder()
            .login("test")
            .lastName("Test")
            .password(Password.builder().clearText("text").build())
            .build();

    this.userCase.createUser(CreateUserRequest.builder().user(user).build());

    verify(passwordHasher).hash("text");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreateWithExistsLogin() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.userRepository.resolveLogin(anyString())).thenReturn(Optional.of("345"));

    User user = User.builder().login("test").lastName("Test").build();

    LoginAlreadyExistsException e =
        assertThrows(
            LoginAlreadyExistsException.class,
            () -> this.userCase.createUser(CreateUserRequest.builder().user(user).build()));

    assertEquals("test", e.getLogin(), "unexpected login");
    assertEquals("345", e.getOwner(), "unexpected owner");
    assertNotNull(e.getMessage(), "message is null");
  }
}
