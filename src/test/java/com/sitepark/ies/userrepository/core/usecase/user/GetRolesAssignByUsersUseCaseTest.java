package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetRolesAssignByUsersUseCaseTest {

  private RoleAssigner roleAssigner;
  private UserEntityAuthorizationService userEntityAuthorizationService;

  private GetRolesAssignByUsersUseCase useCase;

  @BeforeEach
  void setUp() {
    this.roleAssigner = mock();
    this.userEntityAuthorizationService = mock();

    this.useCase = new GetRolesAssignByUsersUseCase(roleAssigner, userEntityAuthorizationService);
  }

  @Test
  void testNotReadableThrowsAccessDenied() {
    when(this.userEntityAuthorizationService.isReadable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.getRolesAssignByUsers(List.of("1", "2")),
        "Should throw AccessDeniedException when users are not readable");
  }

  @Test
  void testGetRolesAssignByUsersCallsRoleAssigner() {
    when(this.userEntityAuthorizationService.isReadable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(anyList()))
        .thenReturn(UserRoleAssignment.builder().build());

    this.useCase.getRolesAssignByUsers(List.of("1", "2"));

    verify(this.roleAssigner).getRolesAssignByUsers(List.of("1", "2"));
  }

  @Test
  void testGetRolesAssignByUsersReturnsAssignments() {
    UserRoleAssignment expected =
        UserRoleAssignment.builder().assignments("1", List.of("role1", "role2")).build();

    when(this.userEntityAuthorizationService.isReadable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(anyList())).thenReturn(expected);

    UserRoleAssignment result = this.useCase.getRolesAssignByUsers(List.of("1", "2"));

    assertEquals(expected, result, "Should return the role assignments from RoleAssigner");
  }
}
