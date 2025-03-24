package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PurgeUser {

  private final UserRepository repository;

  private final IdentifierResolver identifierResolver;

  private final AccessTokenRepository accessTokenRepository;

  private final ExtensionsNotifier extensionsNotifier;

  private final AccessControl accessControl;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  PurgeUser(
      UserRepository repository,
      IdentifierResolver identifierResolver,
      ExtensionsNotifier extensionsNotifier,
      AccessControl accessControl,
      AccessTokenRepository accessTokenRepository) {

    this.repository = repository;
    this.extensionsNotifier = extensionsNotifier;
    this.identifierResolver = identifierResolver;
    this.accessControl = accessControl;
    this.accessTokenRepository = accessTokenRepository;
  }

  public void purgeUser(Identifier identifier) {

    String id = this.identifierResolver.resolveIdentifier(identifier);

    if (!this.accessControl.isUserRemovable()) {
      throw new AccessDeniedException("Not allowed to remove user");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("purge user: {}", id);
    }

    this.repository.remove(id);

    this.accessTokenRepository.purgeByUser(id);

    this.extensionsNotifier.notifyPurge(id);
  }
}
