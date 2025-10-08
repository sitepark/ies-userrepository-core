package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import org.junit.jupiter.api.Test;

class PurgeUserTest {

  @Test
  void testAccessDenied() {

    AccessControl accessControl = mock(AccessControl.class);
    IdentifierResolver identifierResolver = mock();
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    when(accessControl.isUserRemovable()).thenReturn(false);

    var purgeEntity = new PurgeUser(null, identifierResolver, extensionsNotifier, accessControl);
    assertThrows(AccessDeniedException.class, () -> purgeEntity.purgeUser(Identifier.ofId("10")));
  }

  @SuppressWarnings("PMD")
  @Test
  void testPurge() {

    IdentifierResolver identifierResolver = mock();
    when(identifierResolver.resolveIdentifier(any())).thenReturn("10");
    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserRemovable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    UserRepository repository = mock(UserRepository.class);

    PurgeUser purgeEntity =
        new PurgeUser(repository, identifierResolver, extensionsNotifier, accessControl);
    purgeEntity.purgeUser(Identifier.ofId("10"));

    verify(repository).remove("10");
    verify(extensionsNotifier).notifyPurge("10");
  }
}
