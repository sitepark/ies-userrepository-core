package com.sitepark.ies.userrepository.core.usecase;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;

public class CreateImpersonationToken {

	private final AccessTokenRepository repository;

	private final AccessControl accessControl;

	private static Logger LOGGER = LogManager.getLogger();

	@Inject
	protected CreateImpersonationToken(
			AccessTokenRepository repository,
			AccessControl accessControl) {
		this.repository = repository;
		this.accessControl = accessControl;
	}

	public AccessToken createPersonalAccessToken(AccessToken accessToken) {

		AccessToken accessTokenToCreate = accessToken.toBuilder()
			.impersonation(true)
			.build();

		if (!this.accessControl.isImpersonationTokensManageable()) {
			throw new AccessDenied("Not allowed manage impersonation tokens");
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("create: {}", accessTokenToCreate);
		}

		return this.repository.create(accessTokenToCreate);
	}
}
