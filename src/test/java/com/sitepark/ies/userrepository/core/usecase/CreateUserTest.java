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

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
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

    User user = User.builder().login("test").lastName("Test").build();

    var createUserUseCase =
        new CreateUser(repository, null, accessControl, extensionsNotifier, passwordHasher);
    assertThrows(AccessDeniedException.class, () -> createUserUseCase.createUser(user, null));
  }

  @Test
  void testWithId() {

    User user = User.builder().id("123").login("test").build();

    var createUserUseCase = new CreateUser(null, null, null, null, null);
    assertThrows(IllegalArgumentException.class, () -> createUserUseCase.createUser(user, null));
  }

  @Test
  void testAnchorAlreadyExists() {

    UserRepository repository = mock();
    when(repository.resolveAnchor(Anchor.ofString("test.user"))).thenReturn(Optional.of("123"));
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserCreatable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    User user = User.builder().anchor("test.user").login("test").lastName("Test").build();

    var createUserUseCase =
        new CreateUser(repository, null, accessControl, extensionsNotifier, passwordHasher);

    assertThrows(
        AnchorAlreadyExistsException.class, () -> createUserUseCase.createUser(user, null));
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreateWithRoles() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(repository.create(any())).thenReturn("123");
    RoleAssigner roleAssigner = mock();

    User user = User.builder().login("test").lastName("Test").build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, extensionsNotifier, passwordHasher);

    String id = createUserUseCase.createUser(user, new String[] {"333"});

    assertEquals("123", id, "unexpected id");

    verify(repository).create(eq(user));
    verify(roleAssigner).assignRolesToUsers(List.of("123"), List.of("333"));
  }

  @Test
  void testCreateWithAnchor() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(repository.create(any())).thenReturn("123");
    RoleAssigner roleAssigner = mock();

    User user = User.builder().anchor("test.anchor").login("test").lastName("Test").build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, extensionsNotifier, passwordHasher);

    createUserUseCase.createUser(user, null);

    verify(repository).create(eq(user));
  }

  @Test
  void testCreateWithPassword() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    when(repository.create(any())).thenReturn("123");
    RoleAssigner roleAssigner = mock();

    User user =
        User.builder()
            .login("test")
            .lastName("Test")
            .password(Password.builder().clearText("text").build())
            .build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, extensionsNotifier, passwordHasher);

    createUserUseCase.createUser(user, null);

    verify(passwordHasher).hash("text");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreateWithExistsLogin() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreatable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();
    PasswordHasher passwordHasher = mock(PasswordHasher.class);

    UserRepository repository = mock();
    when(repository.resolveLogin(anyString())).thenReturn(Optional.of("345"));
    RoleAssigner roleAssigner = mock();

    User user = User.builder().login("test").lastName("Test").roleIds("333").build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, extensionsNotifier, passwordHasher);

    LoginAlreadyExistsException e =
        assertThrows(
            LoginAlreadyExistsException.class, () -> createUserUseCase.createUser(user, null));
    assertEquals("test", e.getLogin(), "unexpected login");
    assertEquals("345", e.getOwner(), "unexpected owner");
    assertNotNull(e.getMessage(), "message is null");
  }
}
