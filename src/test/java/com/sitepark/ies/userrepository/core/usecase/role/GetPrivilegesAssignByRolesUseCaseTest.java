package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetPrivilegesAssignByRolesUseCaseTest {

  private RoleAssigner roleAssigner;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;

  private GetPrivilegesAssignByRolesUseCase useCase;

  @BeforeEach
  void setUp() {
    this.roleAssigner = mock();
    this.roleEntityAuthorizationService = mock();

    this.useCase =
        new GetPrivilegesAssignByRolesUseCase(roleAssigner, roleEntityAuthorizationService);
  }

  @Test
  void testNotReadableThrowsAccessDenied() {
    when(this.roleEntityAuthorizationService.isReadable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.getPrivilegesAssignByRoles(List.of("1", "2")),
        "Should throw AccessDeniedException when roles are not readable");
  }

  @Test
  void testGetPrivilegesAssignByRolesCallsRoleAssigner() {
    when(this.roleEntityAuthorizationService.isReadable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(anyList()))
        .thenReturn(RolePrivilegeAssignment.builder().build());

    this.useCase.getPrivilegesAssignByRoles(List.of("1", "2"));

    verify(this.roleAssigner).getPrivilegesAssignByRoles(List.of("1", "2"));
  }

  @Test
  void testGetPrivilegesAssignByRolesReturnsAssignments() {
    RolePrivilegeAssignment expected =
        RolePrivilegeAssignment.builder().assignments("1", List.of("priv1", "priv2")).build();

    when(this.roleEntityAuthorizationService.isReadable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(anyList())).thenReturn(expected);

    RolePrivilegeAssignment result = this.useCase.getPrivilegesAssignByRoles(List.of("1", "2"));

    assertEquals(expected, result, "Should return the privilege assignments from RoleAssigner");
  }
}
