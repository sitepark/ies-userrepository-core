package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import org.junit.jupiter.api.Test;

class PurgeUserTest {

  @Test
  void testAccessDenied() {

    AccessControl accessControl = mock(AccessControl.class);
    IdentifierResolver identifierResolver = mock();
    AccessTokenRepository accessTokenRepository = mock(AccessTokenRepository.class);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    when(accessControl.isUserRemovable(anyString())).thenReturn(false);

    var purgeEntity =
        new PurgeUser(
            null, identifierResolver, extensionsNotifier, accessControl, accessTokenRepository);
    assertThrows(
        AccessDeniedException.class,
        () -> {
          purgeEntity.purgeUser(Identifier.ofId("10"));
        });
  }

  @SuppressWarnings("PMD")
  @Test
  void testPurge() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("10");
    AccessControl accessControl = mock(AccessControl.class);
    AccessTokenRepository accessTokenRepository = mock(AccessTokenRepository.class);
    when(accessControl.isUserRemovable(anyString())).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    UserRepository repository = mock(UserRepository.class);

    PurgeUser purgeEntity =
        new PurgeUser(
            repository,
            identifierResolver,
            extensionsNotifier,
            accessControl,
            accessTokenRepository);
    purgeEntity.purgeUser(Identifier.ofId("10"));

    verify(repository).remove("10");
    verify(extensionsNotifier).notifyPurge("10");
    verify(accessTokenRepository).purgeByUser("10");
  }
}
