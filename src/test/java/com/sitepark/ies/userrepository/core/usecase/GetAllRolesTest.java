package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetAllRolesTest {

  @Test
  void testAccessDenied() {

    RoleRepository roleRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isRoleReadable()).thenReturn(false);

    GetAllRoles getAllRoles = new GetAllRoles(roleRepository, accessControl);

    assertThrows(AccessDeniedException.class, getAllRoles::getAllRoles);
  }

  @Test
  void testGet() {

    Role role = Role.builder().id("123").name("test").build();

    RoleRepository roleRepository = mock();
    when(roleRepository.getAll()).thenReturn(List.of(role));
    AccessControl accessControl = mock();
    when(accessControl.isRoleReadable()).thenReturn(true);

    GetAllRoles getAllRoles = new GetAllRoles(roleRepository, accessControl);

    assertEquals(List.of(role), getAllRoles.getAllRoles(), "Unexpected result");
  }
}
