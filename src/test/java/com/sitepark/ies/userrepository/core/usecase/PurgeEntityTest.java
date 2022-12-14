package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class PurgeEntityTest {

	@Test
	void testAccessDenied() {

		AccessControl accessControl = mock(AccessControl.class);
		ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
		when(accessControl.isUserRemovable(anyLong())).thenReturn(false);

		var purgeEntity = new PurgeUser(
				null,
				extensionsNotifier,
				accessControl);
		assertThrows(AccessDenied.class, () -> {
			purgeEntity.purgeUser(10L);
		});
	}

	@SuppressWarnings("PMD")
	@Test
	void testPurge() {

		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isUserRemovable(anyLong())).thenReturn(true);
		ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

		UserRepository repository = mock(UserRepository.class);

		PurgeUser purgeEntity = new PurgeUser(
				repository,
				extensionsNotifier,
				accessControl);
		purgeEntity.purgeUser(10L);

		verify(repository).removeUser(10L);
		verify(extensionsNotifier).notifyPurge(10L);
	}
}
