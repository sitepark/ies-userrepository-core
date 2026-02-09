package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetUserUseCaseTest {

  private UserRepository userRepository;
  private UserEntityAuthorizationService userAuthorizationService;
  private GetUserUseCase getUserUseCase;

  @BeforeEach
  void setUp() {
    this.userRepository = mock();
    this.userAuthorizationService = mock();
    this.getUserUseCase = new GetUserUseCase(userRepository, userAuthorizationService);
  }

  @Test
  void testAccessDeniedGetWithId() {

    when(userAuthorizationService.isReadable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class, () -> getUserUseCase.getUser(Identifier.ofString("123")));
  }

  @Test
  void testAccessDeniedGetWithAnchor() {

    when(userRepository.resolveAnchor(any())).thenReturn(Optional.of("123"));
    when(userAuthorizationService.isReadable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class, () -> getUserUseCase.getUser(Identifier.ofString("abc")));
  }

  @Test
  void testGet() {

    User storedUser = User.builder().id("123").login("test").build();
    when(userRepository.get("123")).thenReturn(Optional.of(storedUser));
    when(userAuthorizationService.isReadable(anyString())).thenReturn(true);

    User expectedUser = User.builder().id("123").login("test").build();

    User user = getUserUseCase.getUser(Identifier.ofString("123"));

    assertEquals(expectedUser, user, "unexpected user");
  }

  @Test
  void testGetUserNotFound() {

    when(userAuthorizationService.isReadable(anyString())).thenReturn(true);

    assertThrows(
        UserNotFoundException.class, () -> getUserUseCase.getUser(Identifier.ofString("123")));
  }
}
