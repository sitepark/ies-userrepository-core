package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PurgeUserUseCase {

  private final UserRepository repository;

  private final ExtensionsNotifier extensionsNotifier;

  private final UserEntityAuthorizationService userEntityAuthorizationService;

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject
  PurgeUserUseCase(
      UserRepository repository,
      ExtensionsNotifier extensionsNotifier,
      UserEntityAuthorizationService userEntityAuthorizationService) {

    this.repository = repository;
    this.extensionsNotifier = extensionsNotifier;
    this.userEntityAuthorizationService = userEntityAuthorizationService;
  }

  public void purgeUser(Identifier identifier) {

    String id = IdentifierResolver.create(this.repository).resolve(identifier);

    if (!this.userEntityAuthorizationService.isRemovable(id)) {
      throw new AccessDeniedException("Not allowed to remove user");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("purge user: {}", id);
    }

    this.repository.remove(id);

    this.extensionsNotifier.notifyPurge(id);
  }
}
