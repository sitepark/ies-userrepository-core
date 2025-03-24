package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AnchorNotFoundException;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UpdateUserTest {

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testAccessDeniedUpdate() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserWritable()).thenReturn(false);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    User user = User.builder().id("123").login("test").build();

    var updateUserUseCase =
        new UpdateUser(null, identifierResolver, null, accessControl, extensionsNotifier);
    assertThrows(AccessDeniedException.class, () -> updateUserUseCase.updateUser(user));

    verify(accessControl).isUserWritable();
  }

  @Test
  void testWithoutAnchorAndId() {

    User user = User.builder().login("test").build();

    var updateUserUseCase = new UpdateUser(null, null, null, null, null);
    assertThrows(IllegalArgumentException.class, () -> updateUserUseCase.updateUser(user));
  }

  @Test
  void testUpdateUserNotFound() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    UserRepository repository = mock(UserRepository.class);

    var updateUserUseCase =
        new UpdateUser(repository, identifierResolver, null, accessControl, extensionsNotifier);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    assertThrows(UserNotFoundException.class, () -> updateUserUseCase.updateUser(user));
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testChangeLoginToAlreadyExistsLogin() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    RoleAssigner roleAssigner = mock();

    UserRepository repository = mock(UserRepository.class);
    when(repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(repository.resolveLogin("test2")).thenReturn(Optional.of("55"));

    User storedUser = User.builder().id("123").anchor("user.test").login("test").build();
    when(repository.get(anyString())).thenReturn(Optional.of(storedUser));

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().id("123").anchor("user.test").login("test2").build();

    assertThrows(LoginAlreadyExistsException.class, () -> updateUserUseCase.updateUser(user));
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateUnchanged() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    RoleAssigner roleAssigner = mock();

    UserRepository repository = mock(UserRepository.class);
    when(repository.resolveLogin("test")).thenReturn(Optional.of("123"));

    User storedUser = User.builder().id("123").anchor("user.test").login("test").build();
    when(repository.get(anyString())).thenReturn(Optional.of(storedUser));

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().id("123").anchor("user.test").login("test").build();

    String id = updateUserUseCase.updateUser(user);
    assertEquals("123", id, "unexpected id");

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());

    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateViaIdWithStoredAnchor() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    RoleAssigner roleAssigner = mock();

    UserRepository repository = mock(UserRepository.class);
    when(repository.resolveLogin("test")).thenReturn(Optional.of("123"));

    User storedUser =
        User.builder().id("123").anchor("user.test").login("test").lastName("A").build();
    when(repository.get(anyString())).thenReturn(Optional.of(storedUser));

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().id("123").login("test").lastName("B").build();

    updateUserUseCase.updateUser(user);

    User effectiveUser =
        User.builder().id("123").login("test").anchor("user.test").lastName("B").build();

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).update(effectiveUser);

    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testViaAnchor() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    when(repository.resolveAnchor(Anchor.ofString("user.test"))).thenReturn(Optional.of("123"));
    RoleAssigner roleAssigner = mock();

    User storedUser =
        User.builder().id("123").login("test").anchor("user.test").lastName("A").build();
    when(repository.get(anyString())).thenReturn(Optional.of(storedUser));

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().login("test").anchor("user.test").lastName("B").build();

    updateUserUseCase.updateUser(user);

    User effectiveUser =
        User.builder().id("123").login("test").anchor("user.test").lastName("B").build();

    verify(repository).get(anyString());
    verify(identifierResolver).resolveIdentifier(any());
    verify(repository).resolveLogin(anyString());
    verify(repository).update(eq(effectiveUser));
    verify(extensionsNotifier).notifyUpdated(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateAnchor() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveLogin("test")).thenReturn(Optional.of("123"));
    RoleAssigner roleAssigner = mock();

    User storedUser = User.builder().id("123").login("test").anchor("user.test").build();
    when(repository.get(anyString())).thenReturn(Optional.of(storedUser));

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().id("123").login("test").anchor("user.test2").build();

    updateUserUseCase.updateUser(user);

    User effectiveUser = User.builder().id("123").login("test").anchor("user.test2").build();

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).update(eq(effectiveUser));
    verify(extensionsNotifier).notifyUpdated(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testUpdateLogin() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveLogin("test")).thenReturn(Optional.of("55"));
    when(repository.resolveLogin("test2")).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    User storedUser = User.builder().id("123").login("test").build();
    when(repository.get(anyString())).thenReturn(Optional.of(storedUser));

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().id("123").login("test2").build();

    updateUserUseCase.updateUser(user);

    User effectiveUser = User.builder().id("123").login("test2").build();

    verify(repository).get(anyString());
    verify(repository).resolveLogin(anyString());
    verify(repository).update(eq(effectiveUser));
    verify(extensionsNotifier).notifyUpdated(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  void testUpdateAnchorNotFound() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenThrow(AnchorNotFoundException.class);
    AccessControl accessControl = mock();
    when(accessControl.isUserWritable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock();

    UserRepository repository = mock();
    when(repository.resolveAnchor(Anchor.ofString("user.test"))).thenReturn(Optional.empty());
    RoleAssigner roleAssigner = mock();

    var updateUserUseCase =
        new UpdateUser(
            repository, identifierResolver, roleAssigner, accessControl, extensionsNotifier);

    User user = User.builder().login("test").anchor("user.test2").build();

    assertThrows(AnchorNotFoundException.class, () -> updateUserUseCase.updateUser(user));
  }
}
