package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.domain.Anchor;
import com.sitepark.ies.sharedkernel.anchor.exception.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Password;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.IdGenerator;
import com.sitepark.ies.userrepository.core.port.PasswordHasher;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateUserTest {

  @Test
  void testAccessDeniedCreate() {

    UserRepository repository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserCreatable()).thenReturn(false);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    User user = User.builder().login("test").build();

    var createUserUseCase =
        new CreateUser(repository, null, accessControl, null, extensionsNotifier, passwordHasher);
    assertThrows(AccessDeniedException.class, () -> createUserUseCase.createUser(user));
  }

  @Test
  void testWithId() {

    User user = User.builder().id("123").login("test").build();

    var createUserUseCase = new CreateUser(null, null, null, null, null, null);
    assertThrows(IllegalArgumentException.class, () -> createUserUseCase.createUser(user));
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testAnchorAlreadyExists() {

    UserRepository repository = mock();
    when(repository.resolveAnchor(Anchor.ofString("test.user"))).thenReturn(Optional.of("123"));
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserCreatable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    User user = User.builder().anchor("test.user").login("test").build();

    var createUserUseCase =
        new CreateUser(repository, null, accessControl, null, extensionsNotifier, passwordHasher);

    assertThrows(AnchorAlreadyExistsException.class, () -> createUserUseCase.createUser(user));
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreateWithRoles() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    User user = User.builder().login("test").roleIds("333").build();

    var createUserUseCase =
        new CreateUser(
            repository,
            roleAssigner,
            accessControl,
            idGenerator,
            extensionsNotifier,
            passwordHasher);

    String id = createUserUseCase.createUser(user);

    assertEquals("123", id, "unexpected id");

    User effectiveUser = User.builder().id("123").login("test").roleIds("333").build();

    verify(repository).create(eq(effectiveUser));
    verify(roleAssigner).assignRoleToUser(List.of("333"), List.of("123"));
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testCreateWithAnchor() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    User user = User.builder().anchor("test.anchor").login("test").build();

    var createUserUseCase =
        new CreateUser(
            repository,
            roleAssigner,
            accessControl,
            idGenerator,
            extensionsNotifier,
            passwordHasher);

    createUserUseCase.createUser(user);

    User effectiveUser = User.builder().id("123").anchor("test.anchor").login("test").build();

    verify(repository).create(eq(effectiveUser));
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testCreateWithPassword() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    User user =
        User.builder().login("test").password(Password.builder().clearText("text").build()).build();

    var createUserUseCase =
        new CreateUser(
            repository,
            roleAssigner,
            accessControl,
            idGenerator,
            extensionsNotifier,
            passwordHasher);

    createUserUseCase.createUser(user);

    verify(passwordHasher).hash("text");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreateWithExistsLogin() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveLogin(anyString())).thenReturn(Optional.of("345"));
    RoleAssigner roleAssigner = mock();

    User user = User.builder().login("test").roleIds("333").build();

    var createUserUseCase =
        new CreateUser(
            repository,
            roleAssigner,
            accessControl,
            idGenerator,
            extensionsNotifier,
            passwordHasher);

    LoginAlreadyExistsException e =
        assertThrows(LoginAlreadyExistsException.class, () -> createUserUseCase.createUser(user));
    assertEquals("test", e.getLogin(), "unexpected login");
    assertEquals("345", e.getOwner(), "unexpected owner");
    assertNotNull(e.getMessage(), "message is null");
  }
}
