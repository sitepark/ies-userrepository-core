package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateImpersonationTokenTest {

  @Test
  void testAccessDenied() {

    AccessTokenRepository accessTokenRepository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isImpersonationTokensManageable()).thenReturn(false);
    UserRepository userRepository = mock(UserRepository.class);

    AccessToken accessToken = AccessToken.builder().user("123").name("Test Token").build();

    var createImpersonationToken =
        new CreateImpersonationToken(accessTokenRepository, accessControl, userRepository);

    assertThrows(
        AccessDeniedException.class,
        () -> {
          createImpersonationToken.createPersonalAccessToken(accessToken);
        });

    verify(accessControl).isImpersonationTokensManageable();
  }

  @Test
  void testUserNotFound() {

    AccessTokenRepository accessTokenRepository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isImpersonationTokensManageable()).thenReturn(true);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.get(anyString())).thenReturn(Optional.empty());

    AccessToken accessToken = AccessToken.builder().user("123").name("Test Token").build();

    var createImpersonationToken =
        new CreateImpersonationToken(accessTokenRepository, accessControl, userRepository);

    assertThrows(
        UserNotFoundException.class,
        () -> {
          createImpersonationToken.createPersonalAccessToken(accessToken);
        });
  }

  @Test
  void testCreate() {

    AccessTokenRepository accessTokenRepository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isImpersonationTokensManageable()).thenReturn(true);
    UserRepository userRepository = mock(UserRepository.class);
    User user = mock(User.class);
    when(userRepository.get(anyString())).thenReturn(Optional.of(user));

    AccessToken accessToken = AccessToken.builder().user("123").name("Test Token").build();

    var createImpersonationToken =
        new CreateImpersonationToken(accessTokenRepository, accessControl, userRepository);

    createImpersonationToken.createPersonalAccessToken(accessToken);

    verify(accessTokenRepository).create(any());
  }
}
