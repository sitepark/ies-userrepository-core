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
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateUserTest {

  private UserRepository repository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;
  private PasswordHasher passwordHasher;
  private CreateUser userCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(UserRepository.class);
    this.roleAssigner = mock(RoleAssigner.class);
    this.accessControl = mock(AccessControl.class);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    this.passwordHasher = mock(PasswordHasher.class);

    AuditLogService auditLogService = mock();
    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.userCase =
        new CreateUser(
            this.repository,
            this.roleAssigner,
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
    when(this.repository.resolveAnchor(Anchor.ofString("test.user")))
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
    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(this.repository.create(any())).thenReturn("123");

    User user = User.builder().login("test").lastName("Test").build();

    String id =
        this.userCase.createUser(CreateUserRequest.builder().user(user).roleId("333").build());

    assertEquals("123", id, "unexpected id");

    verify(this.repository).create(eq(user));
    verify(this.roleAssigner).assignRolesToUsers(List.of("123"), List.of("333"));
  }

  @Test
  void testCreateWithAnchor() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(this.repository.create(any())).thenReturn("123");

    User user = User.builder().anchor("test.anchor").login("test").lastName("Test").build();

    this.userCase.createUser(CreateUserRequest.builder().user(user).build());

    verify(repository).create(eq(user));
  }

  @Test
  void testCreateWithPassword() {

    when(this.accessControl.isUserCreatable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(this.repository.create(any())).thenReturn("123");

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
    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.of("345"));

    User user = User.builder().login("test").lastName("Test").roleIds("333").build();

    LoginAlreadyExistsException e =
        assertThrows(
            LoginAlreadyExistsException.class,
            () -> this.userCase.createUser(CreateUserRequest.builder().user(user).build()));

    assertEquals("test", e.getLogin(), "unexpected login");
    assertEquals("345", e.getOwner(), "unexpected owner");
    assertNotNull(e.getMessage(), "message is null");
  }
}
