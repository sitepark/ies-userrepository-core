package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RevokeImpersonationToken {

  private final AccessTokenRepository repository;

  private final AccessControl accessControl;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  protected RevokeImpersonationToken(
      AccessTokenRepository repository, AccessControl accessControl) {
    this.repository = repository;
    this.accessControl = accessControl;
  }

  public void revokeImpersonationToken(String user, String id) {

    if (!this.accessControl.isImpersonationTokensManageable()) {
      throw new AccessDeniedException("Not allowed manage impersonation tokens");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("revoke impersonation token: {}/{}", user, id);
    }

    this.repository.revoke(user, id);
  }
}
