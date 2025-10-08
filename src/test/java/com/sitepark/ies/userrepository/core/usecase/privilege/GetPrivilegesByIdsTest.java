package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetPrivilegesByIdsTest {

  @Test
  void testAccessDenied() {

    PrivilegeRepository privilegeRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isPrivilegeReadable()).thenReturn(false);

    GetPrivilegesByIds getPrivilegesByIds =
        new GetPrivilegesByIds(privilegeRepository, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getPrivilegesByIds.getPrivilegesByIds(List.of("123")));
  }

  @Test
  void testGet() {

    Privilege privilege = Privilege.builder().id("123").name("test").build();

    PrivilegeRepository privilegeRepository = mock();
    when(privilegeRepository.getByIds(anyList())).thenReturn(List.of(privilege));
    AccessControl accessControl = mock();
    when(accessControl.isPrivilegeReadable()).thenReturn(true);

    GetPrivilegesByIds getPrivilegesByIds =
        new GetPrivilegesByIds(privilegeRepository, accessControl);

    assertEquals(
        List.of(privilege),
        getPrivilegesByIds.getPrivilegesByIds(List.of("123")),
        "Unexpected result");
  }
}
