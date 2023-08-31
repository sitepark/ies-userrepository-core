package com.sitepark.ies.userrepository.core.usecase;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public final class PurgeUser {

	private final UserRepository repository;

	private final AccessTokenRepository accessTokenRepository;

	private final ExtensionsNotifier extensionsNotifier;

	private final AccessControl accessControl;

	private static Logger LOGGER = LogManager.getLogger();

	@Inject
	protected PurgeUser(
			UserRepository repository,
			ExtensionsNotifier extensionsNotifier,
			AccessControl accessControl,
			AccessTokenRepository accessTokenRepository) {

		this.repository = repository;
		this.extensionsNotifier = extensionsNotifier;
		this.accessControl = accessControl;
		this.accessTokenRepository = accessTokenRepository;
	}

	public void purgeUser(long id) {

		if (!this.accessControl.isUserRemovable(id)) {
			throw new AccessDenied("Not allowed to remove user " + id);
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("purge user: {}", id);
		}

		this.repository.remove(id);

		this.accessTokenRepository.purgeByUser(id);

		this.extensionsNotifier.notifyPurge(id);
	}
}
