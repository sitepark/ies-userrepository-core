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

class GetRolesByPrivilegeIdsUseCaseTest {

  @Test
  void testAccessDenied() {

    RoleRepository roleRepository = mock();
    RoleEntityAuthorizationService roleAuthorizationService = mock();
    when(roleAuthorizationService.isReadable(anyList())).thenReturn(false);

    GetRolesByPrivilegeIdsUseCase getRolesByPrivileges =
        new GetRolesByPrivilegeIdsUseCase(roleRepository, roleAuthorizationService);

    assertThrows(
        AccessDeniedException.class,
        () -> getRolesByPrivileges.getRolesByPrivilegeIds(List.of("123")));
  }

  @Test
  void testGet() {

    Role role = Role.builder().id("123").name("test").build();

    RoleRepository roleRepository = mock();
    when(roleRepository.getByPrivilegeIds(anyList())).thenReturn(List.of(role));
    RoleEntityAuthorizationService roleAuthorizationService = mock();
    when(roleAuthorizationService.isReadable(anyList())).thenReturn(true);

    GetRolesByPrivilegeIdsUseCase getRolesByPrivileges =
        new GetRolesByPrivilegeIdsUseCase(roleRepository, roleAuthorizationService);

    assertEquals(
        List.of(role),
        getRolesByPrivileges.getRolesByPrivilegeIds(List.of("123")),
        "Unexpected result");
  }
}
