package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetUserUseCaseTest {

  @Test
  void testAccessDeniedGetWithId() {

    UserRepository userRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    GetUserUseCase getUserUseCase = new GetUserUseCase(userRepository, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getUserUseCase.getUser(Identifier.ofString("123")));
  }

  @Test
  void testAccessDeniedGetWithAnchor() {

    UserRepository userRepository = mock();
    when(userRepository.resolveAnchor(any())).thenReturn(Optional.of("123"));
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    GetUserUseCase getUserUseCase = new GetUserUseCase(userRepository, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getUserUseCase.getUser(Identifier.ofString("abc")));
  }

  @Test
  void testGet() {

    UserRepository userRepository = mock();
    User storedUser = User.builder().id("123").login("test").build();
    when(userRepository.get("123")).thenReturn(Optional.of(storedUser));
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    GetUserUseCase getUserUseCase = new GetUserUseCase(userRepository, accessControl);

    User expectedUser = User.builder().id("123").login("test").build();

    User user = getUserUseCase.getUser(Identifier.ofString("123"));

    assertEquals(expectedUser, user, "unexpected user");
  }

  @Test
  void testGetUserNotFound() {

    UserRepository userRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    GetUserUseCase getUserUseCase = new GetUserUseCase(userRepository, accessControl);

    assertThrows(
        UserNotFoundException.class, () -> getUserUseCase.getUser(Identifier.ofString("123")));
  }
}
