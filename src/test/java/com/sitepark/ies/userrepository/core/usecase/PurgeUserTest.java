package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class PurgeUserTest {

	@Test
	void testAccessDenied() {

		AccessControl accessControl = mock(AccessControl.class);
		AccessTokenRepository accessTokenRepository = mock(AccessTokenRepository.class);
		ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
		when(accessControl.isUserRemovable(anyLong())).thenReturn(false);

		var purgeEntity = new PurgeUser(
				null,
				extensionsNotifier,
				accessControl,
				accessTokenRepository);
		assertThrows(AccessDeniedException.class, () -> {
			purgeEntity.purgeUser(10L);
		});
	}

	@SuppressWarnings("PMD")
	@Test
	void testPurge() {

		AccessControl accessControl = mock(AccessControl.class);
		AccessTokenRepository accessTokenRepository = mock(AccessTokenRepository.class);
		when(accessControl.isUserRemovable(anyLong())).thenReturn(true);
		ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

		UserRepository repository = mock(UserRepository.class);

		PurgeUser purgeEntity = new PurgeUser(
				repository,
				extensionsNotifier,
				accessControl,
				accessTokenRepository);
		purgeEntity.purgeUser(10L);

		verify(repository).remove(10L);
		verify(extensionsNotifier).notifyPurge(10L);
		verify(accessTokenRepository).purgeByUser(10L);
	}
}
