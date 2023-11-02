package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;

class RevokeImpersonationTokenTest {

	@Test
	void testAccessDenied() {

		AccessTokenRepository accessTokenRepository = mock();
		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isImpersonationTokensManageable()).thenReturn(false);

		var revokeImpersonationToken = new RevokeImpersonationToken(
				accessTokenRepository,
				accessControl);

		assertThrows(AccessDeniedException.class, () -> {
			revokeImpersonationToken.revokeImpersonationToken(1L, 2L);
		});

		verify(accessControl).isImpersonationTokensManageable();
	}

	@Test
	void testRevoke() {

		AccessTokenRepository accessTokenRepository = mock();
		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isImpersonationTokensManageable()).thenReturn(true);

		var revokeImpersonationToken = new RevokeImpersonationToken(
				accessTokenRepository,
				accessControl);

		revokeImpersonationToken.revokeImpersonationToken(1L, 2L);

		verify(accessTokenRepository).revoke(1L, 2L);
	}




}
