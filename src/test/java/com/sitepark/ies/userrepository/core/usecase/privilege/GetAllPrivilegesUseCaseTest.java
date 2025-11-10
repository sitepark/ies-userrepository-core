package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetAllPrivilegesUseCaseTest {

  @Test
  void testAccessDenied() {

    PrivilegeRepository privilegeRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isPrivilegeReadable()).thenReturn(false);

    GetAllPrivilegesUseCase getAllPrivileges =
        new GetAllPrivilegesUseCase(privilegeRepository, accessControl);

    assertThrows(AccessDeniedException.class, getAllPrivileges::getAllPrivileges);
  }

  @Test
  void testGet() {

    Privilege privilege = Privilege.builder().id("123").name("test").build();

    PrivilegeRepository privilegeRepository = mock();
    when(privilegeRepository.getAll()).thenReturn(List.of(privilege));
    AccessControl accessControl = mock();
    when(accessControl.isPrivilegeReadable()).thenReturn(true);

    GetAllPrivilegesUseCase getAllPrivileges =
        new GetAllPrivilegesUseCase(privilegeRepository, accessControl);

    assertEquals(List.of(privilege), getAllPrivileges.getAllPrivileges(), "Unexpected result");
  }
}
