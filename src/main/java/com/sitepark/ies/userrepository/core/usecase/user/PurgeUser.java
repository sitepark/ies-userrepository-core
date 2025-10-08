package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PurgeUser {

  private final UserRepository repository;

  private final IdentifierResolver identifierResolver;

  private final ExtensionsNotifier extensionsNotifier;

  private final AccessControl accessControl;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  PurgeUser(
      UserRepository repository,
      IdentifierResolver identifierResolver,
      ExtensionsNotifier extensionsNotifier,
      AccessControl accessControl) {

    this.repository = repository;
    this.extensionsNotifier = extensionsNotifier;
    this.identifierResolver = identifierResolver;
    this.accessControl = accessControl;
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

    this.extensionsNotifier.notifyPurge(id);
  }
}
