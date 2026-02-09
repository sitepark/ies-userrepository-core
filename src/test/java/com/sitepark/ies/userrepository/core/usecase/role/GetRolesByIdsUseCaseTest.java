package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetRolesByIdsUseCaseTest {

  @Test
  void testAccessDenied() {

    RoleRepository roleRepository = mock();
    RoleEntityAuthorizationService roleAuthorizationService = mock();
    when(roleAuthorizationService.isReadable(anyList())).thenReturn(false);

    GetRolesByIdsUseCase getRolesByIds =
        new GetRolesByIdsUseCase(roleRepository, roleAuthorizationService);

    assertThrows(AccessDeniedException.class, () -> getRolesByIds.getRolesByIds(List.of("123")));
  }

  @Test
  void testGet() {

    Role role = Role.builder().id("123").name("test").build();

    RoleRepository roleRepository = mock();
    when(roleRepository.getByIds(anyList())).thenReturn(List.of(role));
    RoleEntityAuthorizationService roleAuthorizationService = mock();
    when(roleAuthorizationService.isReadable(anyList())).thenReturn(true);

    GetRolesByIdsUseCase getRolesByIds =
        new GetRolesByIdsUseCase(roleRepository, roleAuthorizationService);

    assertEquals(List.of(role), getRolesByIds.getRolesByIds(List.of("123")), "Unexpected result");
  }
}
