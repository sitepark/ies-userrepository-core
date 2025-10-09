package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PurgeUserUseCaseTest {

  @Test
  void testAccessDenied() {

    AccessControl accessControl = mock(AccessControl.class);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);
    when(accessControl.isUserRemovable()).thenReturn(false);

    var purgeEntity = new PurgeUserUseCase(null, extensionsNotifier, accessControl);
    assertThrows(AccessDeniedException.class, () -> purgeEntity.purgeUser(Identifier.ofId("10")));
  }

  @SuppressWarnings("PMD")
  @Test
  void testPurge() {

    AccessControl accessControl = mock(AccessControl.class);
    when(accessControl.isUserRemovable()).thenReturn(true);
    ExtensionsNotifier extensionsNotifier = mock(ExtensionsNotifier.class);

    UserRepository repository = mock(UserRepository.class);
    when(repository.resolveAnchor(any())).thenReturn(Optional.of("10"));

    PurgeUserUseCase purgeEntity =
        new PurgeUserUseCase(repository, extensionsNotifier, accessControl);
    purgeEntity.purgeUser(Identifier.ofId("10"));

    verify(repository).remove("10");
    verify(extensionsNotifier).notifyPurge("10");
  }
}
