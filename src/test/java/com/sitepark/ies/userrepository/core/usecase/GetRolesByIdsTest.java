package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetRolesByIdsTest {

  @Test
  void testAccessDenied() {

    RoleRepository roleRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isRoleReadable()).thenReturn(false);

    GetRolesByIds getRolesByIds = new GetRolesByIds(roleRepository, accessControl);

    assertThrows(AccessDeniedException.class, () -> getRolesByIds.getRolesByIds(List.of("123")));
  }

  @Test
  void testGet() {

    Role role = Role.builder().id("123").name("test").build();

    RoleRepository roleRepository = mock();
    when(roleRepository.getByIds(anyList())).thenReturn(List.of(role));
    AccessControl accessControl = mock();
    when(accessControl.isRoleReadable()).thenReturn(true);

    GetRolesByIds getRolesByIds = new GetRolesByIds(roleRepository, accessControl);

    assertEquals(List.of(role), getRolesByIds.getRolesByIds(List.of("123")), "Unexpected result");
  }
}
