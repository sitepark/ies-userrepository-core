package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import org.junit.jupiter.api.Test;

class RevokeImpersonationTokenTest {

  @Test
  void testAccessDenied() {

    AccessTokenRepository accessTokenRepository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isImpersonationTokensManageable()).thenReturn(false);

    var revokeImpersonationToken =
        new RevokeImpersonationToken(accessTokenRepository, accessControl);

    assertThrows(
        AccessDeniedException.class,
        () -> revokeImpersonationToken.revokeImpersonationToken("1", "2"));
  }

  @Test
  void testRevoke() {

    AccessTokenRepository accessTokenRepository = mock();
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isImpersonationTokensManageable()).thenReturn(true);

    var revokeImpersonationToken =
        new RevokeImpersonationToken(accessTokenRepository, accessControl);

    revokeImpersonationToken.revokeImpersonationToken("1", "2");

    verify(accessTokenRepository).revoke("1", "2");
  }
}
