package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetUserTest {

  @Test
  void testAccessDeniedGetWithId() {

    UserRepository userRepository = mock();
    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    GetUser getUserUseCase = new GetUser(userRepository, identifierResolver, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getUserUseCase.getUser(Identifier.ofString("123")));
  }

  @Test
  void testAccessDeniedGetWithAnchor() {

    UserRepository userRepository = mock();
    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    GetUser getUserUseCase = new GetUser(userRepository, identifierResolver, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getUserUseCase.getUser(Identifier.ofString("abc")));
  }

  @Test
  void testGet() {

    UserRepository userRepository = mock();
    User storedUser = User.builder().id("123").login("test").roleId("345").build();
    when(userRepository.get("123")).thenReturn(Optional.of(storedUser));
    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    GetUser getUserUseCase = new GetUser(userRepository, identifierResolver, accessControl);

    User expectedUser = User.builder().id("123").login("test").roleIds("345").build();

    User user = getUserUseCase.getUser(Identifier.ofString("123"));

    assertEquals(expectedUser, user, "unexpected user");
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testGetUserNotFound() {

    UserRepository userRepository = mock();
    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    GetUser getUserUseCase = new GetUser(userRepository, identifierResolver, accessControl);

    assertThrows(
        UserNotFoundException.class, () -> getUserUseCase.getUser(Identifier.ofString("123")));
  }
}
