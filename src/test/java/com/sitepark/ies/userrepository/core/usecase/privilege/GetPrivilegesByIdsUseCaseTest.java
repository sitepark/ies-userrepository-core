package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetPrivilegesByIdsUseCaseTest {

  private PrivilegeRepository repository;
  private PrivilegeEntityAuthorizationService privilegeEntityAuthorizationService;
  private GetPrivilegesByIdsUseCase usecase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.privilegeEntityAuthorizationService = mock();
    this.usecase = new GetPrivilegesByIdsUseCase(repository, privilegeEntityAuthorizationService);
  }

  @Test
  void testAccessDenied() {

    when(privilegeEntityAuthorizationService.isReadable(anyList())).thenReturn(false);

    assertThrows(AccessDeniedException.class, () -> usecase.getPrivilegesByIds(List.of("123")));
  }

  @Test
  void testGet() {

    Privilege privilege = Privilege.builder().id("123").name("test").build();
    when(repository.getByIds(anyList())).thenReturn(List.of(privilege));
    when(privilegeEntityAuthorizationService.isReadable(anyList())).thenReturn(true);

    assertEquals(
        List.of(privilege), usecase.getPrivilegesByIds(List.of("123")), "Unexpected result");
  }
}
