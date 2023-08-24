package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;

class CreateImpersonationTokenTest {

	@Test
	void testAccessDenied() {

		AccessTokenRepository accessTokenRepository = mock();
		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isImpersonationTokensManageable()).thenReturn(false);

		AccessToken accessToken = AccessToken.builder().user(123).name("Test Token").build();

		var createImpersonationToken = new CreateImpersonationToken(
				accessTokenRepository,
				accessControl);

		assertThrows(AccessDenied.class, () -> {
			createImpersonationToken.createPersonalAccessToken(accessToken);
		});

		verify(accessControl).isImpersonationTokensManageable();
	}

	@Test
	void testCreate() {

		AccessTokenRepository accessTokenRepository = mock();
		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isImpersonationTokensManageable()).thenReturn(true);

		AccessToken accessToken = AccessToken.builder().user(123).name("Test Token").build();

		var createImpersonationToken = new CreateImpersonationToken(
				accessTokenRepository,
				accessControl);

		createImpersonationToken.createPersonalAccessToken(accessToken);

		verify(accessTokenRepository).create(any());
	}

}
