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

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.entity.role.UserLevelRoles;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.exception.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.IdGenerator;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class CreateUserTest {

  @Test
  void testAccessDeniedCreate() {

    UserRepository repository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserCreateable()).thenReturn(false);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    User user = User.builder().login("test").build();

    var createUserUseCase =
        new CreateUser(repository, null, accessControl, null, extensionsNotifier);
    assertThrows(
        AccessDeniedException.class,
        () -> {
          createUserUseCase.createUser(user);
        });

    verify(accessControl).isUserCreateable();
  }

  @Test
  void testWithId() {

    User user = User.builder().id("123").login("test").build();

    var createUserUseCase = new CreateUser(null, null, null, null, null);
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          createUserUseCase.createUser(user);
        });
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testAnchorAlreadyExists() {

    UserRepository repository = mock();
    when(repository.resolveAnchor(Anchor.ofString("test.user"))).thenReturn(Optional.of("123"));
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserCreateable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    User user = User.builder().anchor("test.user").login("test").build();

    var createUserUseCase =
        new CreateUser(repository, null, accessControl, null, extensionsNotifier);

    AnchorAlreadyExistsException e =
        assertThrows(
            AnchorAlreadyExistsException.class,
            () -> {
              createUserUseCase.createUser(user);
            });

    assertEquals(Anchor.ofString("test.user"), e.getAnchor(), "unexpected anchor");
    assertEquals("123", e.getOwner(), "unexpected owner");
    assertNotNull(e.getMessage(), "message is null");
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testCreateWithRoles() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreateable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    User user = User.builder().login("test").roleList(UserLevelRoles.USER, Ref.ofId("333")).build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, idGenerator, extensionsNotifier);

    createUserUseCase.createUser(user);

    User effectiveUser =
        User.builder()
            .id("123")
            .login("test")
            .roleList(UserLevelRoles.USER, Ref.ofId("333"))
            .build();

    verify(repository).create(eq(effectiveUser));
    verify(roleAssigner)
        .assignRoleToUser(
            Arrays.asList(UserLevelRoles.USER, Ref.ofId("333")), Arrays.asList("123"));
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testCreateWithAnchor() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreateable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(repository.resolveLogin(anyString())).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    User user = User.builder().anchor("test.anchor").login("test").build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, idGenerator, extensionsNotifier);

    createUserUseCase.createUser(user);

    User effectiveUser = User.builder().id("123").anchor("test.anchor").login("test").build();

    verify(repository).create(eq(effectiveUser));
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testCreateWithExistsLogin() {

    AccessControl accessControl = mock();
    when(accessControl.isUserCreateable()).thenReturn(true);
    IdGenerator idGenerator = mock();
    when(idGenerator.generate()).thenReturn("123");
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveLogin(anyString())).thenReturn(Optional.of("345"));
    RoleAssigner roleAssigner = mock();

    User user = User.builder().login("test").roleList(UserLevelRoles.USER, Ref.ofId("333")).build();

    var createUserUseCase =
        new CreateUser(repository, roleAssigner, accessControl, idGenerator, extensionsNotifier);

    LoginAlreadyExistsException e =
        assertThrows(
            LoginAlreadyExistsException.class,
            () -> {
              createUserUseCase.createUser(user);
            });
    assertEquals("test", e.getLogin(), "unexpected login");
    assertEquals("345", e.getOwner(), "unexpected owner");
    assertNotNull(e.getMessage(), "message is null");
  }
}
