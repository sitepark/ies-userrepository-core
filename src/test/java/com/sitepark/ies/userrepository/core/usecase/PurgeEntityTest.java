package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class PurgeEntityTest {

	@Test
	void testAccessDenied() {

		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isEntityRemovable(anyLong())).thenReturn(false);

		var purgeEntity = new PurgeUser(
				null,
				accessControl);
		assertThrows(AccessDenied.class, () -> {
			purgeEntity.purgeEntity(10L);
		});
	}

	@SuppressWarnings("PMD")
	@Test
	void testPurge() {

		AccessControl accessControl = mock(AccessControl.class);
		when(accessControl.isEntityRemovable(anyLong())).thenReturn(true);

		UserRepository repository = mock(UserRepository.class);

		PurgeUser purgeEntity = new PurgeUser(
				repository,
				accessControl);
		purgeEntity.purgeEntity(10L);

		verify(repository).removeEntity(10L);
	}
}
