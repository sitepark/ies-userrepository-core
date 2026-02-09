package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurgeUserUseCaseTest {

  private UserRepository repository;
  private ExtensionsNotifier extensionsNotifier;
  private UserEntityAuthorizationService userAuthorizationService;
  private PurgeUserUseCase usecase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.userAuthorizationService = mock();
    this.extensionsNotifier = mock();
    this.usecase = new PurgeUserUseCase(repository, extensionsNotifier, userAuthorizationService);
  }

  @Test
  void testAccessDenied() {
    when(userAuthorizationService.isRemovable(anyString())).thenReturn(false);
    assertThrows(AccessDeniedException.class, () -> usecase.purgeUser(Identifier.ofId("10")));
  }

  @SuppressWarnings("PMD")
  @Test
  void testPurge() {

    when(userAuthorizationService.isRemovable(anyString())).thenReturn(true);
    when(repository.resolveAnchor(any())).thenReturn(Optional.of("10"));

    usecase.purgeUser(Identifier.ofId("10"));

    verify(repository).remove("10");
    verify(extensionsNotifier).notifyPurge("10");
  }
}
