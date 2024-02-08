package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateImpersonationToken {

  private final AccessTokenRepository repository;

  private final AccessControl accessControl;

  private final UserRepository userRepository;

  private static Logger LOGGER = LogManager.getLogger();

  @Inject
  protected CreateImpersonationToken(
      AccessTokenRepository repository,
      AccessControl accessControl,
      UserRepository userRepository) {
    this.repository = repository;
    this.accessControl = accessControl;
    this.userRepository = userRepository;
  }

  public AccessToken createPersonalAccessToken(AccessToken accessToken) {

    AccessToken accessTokenToCreate = accessToken.toBuilder().impersonation(true).build();

    if (!this.accessControl.isImpersonationTokensManageable()) {
      throw new AccessDeniedException("Not allowed manage impersonation tokens");
    }

    if (this.userRepository.get(accessToken.getUser()).isEmpty()) {
      throw new UserNotFoundException(accessToken.getUser());
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create: {}", accessTokenToCreate);
    }

    return this.repository.create(accessTokenToCreate);
  }
}
