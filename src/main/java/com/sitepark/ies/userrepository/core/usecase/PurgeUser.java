package com.sitepark.ies.userrepository.core.usecase;

import javax.inject.Inject;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public final class PurgeUser {

	private final UserRepository repository;
	private final ExtensionsNotifier extensionsNotifier;
	private final AccessControl accessControl;

	@Inject
	protected PurgeUser(
			UserRepository repository,
			ExtensionsNotifier extensionsNotifier,
			AccessControl accessControl) {

		this.repository = repository;
		this.extensionsNotifier = extensionsNotifier;
		this.accessControl = accessControl;
	}

	public void purgeUser(long id) {

		if (!this.accessControl.isUserRemovable(id)) {
			throw new AccessDenied("Not allowed to remove user " + id);
		}

		this.repository.remove(id);

		this.extensionsNotifier.notifyPurge(id);
	}
}
