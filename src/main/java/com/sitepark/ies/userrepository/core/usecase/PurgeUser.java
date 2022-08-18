package com.sitepark.ies.userrepository.core.usecase;

import javax.inject.Inject;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public final class PurgeUser {

	private final UserRepository repository;
	private final AccessControl accessControl;

	@Inject
	protected PurgeUser(UserRepository repository, AccessControl accessControl) {

		this.repository = repository;
		this.accessControl = accessControl;
	}

	public void purgeEntity(long id) {

		if (!this.accessControl.isEntityRemovable(id)) {
			throw new AccessDenied("Not allowed to remove entity " + id);
		}

		this.repository.removeEntity(id);
	}
}
