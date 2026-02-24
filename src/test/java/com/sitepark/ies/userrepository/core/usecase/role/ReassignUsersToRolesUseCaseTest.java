package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.RoleUserAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReassignUsersToRolesUseCaseTest {

  private RoleAssigner roleAssigner;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;

  private ReassignUsersToRolesUseCase useCase;

  @BeforeEach
  void setUp() {
    UserRepository userRepository = mock();
    RoleRepository roleRepository = mock();
    this.roleAssigner = mock();
    this.roleEntityAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.useCase =
        new ReassignUsersToRolesUseCase(
            userRepository,
            roleRepository,
            this.roleAssigner,
            this.roleEntityAuthorizationService,
            fixedClock);
  }

  @Test
  void testEmptyRequestReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    ReassignUsersToRolesResult result =
        this.useCase.reassignUsersToRoles(ReassignUsersToRolesRequest.builder().build());

    assertFalse(result.wasReassigned(), "Result should indicate skipped when request is empty");
  }

  @Test
  void testNotWritableThrowsAccessDenied() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.reassignUsersToRoles(
                ReassignUsersToRolesRequest.builder()
                    .roleIdentifiers(b -> b.ids("1", "2"))
                    .userIdentifiers(b -> b.ids("3", "4"))
                    .build()),
        "Should throw AccessDeniedException when roles are not writable");
  }

  @Test
  void testNoEffectiveChangesReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(
            RoleUserAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    ReassignUsersToRolesResult result =
        this.useCase.reassignUsersToRoles(
            ReassignUsersToRolesRequest.builder()
                .roleIdentifiers(b -> b.ids("1", "2"))
                .userIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when no effective changes exist");
  }

  @Test
  void testReassignWithNewUsersCallsAssign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(RoleUserAssignment.builder().build());

    this.useCase.reassignUsersToRoles(
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(anyList(), eq(List.of("1")));
  }

  @Test
  void testReassignWithUnassignmentsCallsUnassign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(RoleUserAssignment.builder().assignments("1", List.of("5", "6")).build());

    this.useCase.reassignUsersToRoles(
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).unassignRolesFromUsers(anyList(), eq(List.of("1")));
  }

  @Test
  void testReassignWithEffectiveChangesReturnsReassigned() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(RoleUserAssignment.builder().build());

    ReassignUsersToRolesResult result =
        this.useCase.reassignUsersToRoles(
            ReassignUsersToRolesRequest.builder()
                .roleIdentifiers(b -> b.ids("1", "2"))
                .userIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(
        result.wasReassigned(),
        "Result should indicate reassignment occurred when effective changes exist");
  }

  @Test
  void testEmptyRoleIdentifiersReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    ReassignUsersToRolesResult result =
        this.useCase.reassignUsersToRoles(
            ReassignUsersToRolesRequest.builder().userIdentifiers(b -> b.ids("3", "4")).build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when role identifiers are empty");
  }

  @Test
  void testEmptyUserIdentifiersReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(RoleUserAssignment.builder().build());

    ReassignUsersToRolesResult result =
        this.useCase.reassignUsersToRoles(
            ReassignUsersToRolesRequest.builder().roleIdentifiers(b -> b.ids("1", "2")).build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when user identifiers are empty");
  }

  @Test
  void testPartialMatchCreatesEffectiveAssignments() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(
            RoleUserAssignment.builder()
                .assignments("1", List.of("3"))
                .assignments("2", List.of("3"))
                .build());

    this.useCase.reassignUsersToRoles(
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(List.of("4"), List.of("1"));
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallAssign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(
            RoleUserAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignUsersToRoles(
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).assignRolesToUsers(anyList(), anyList());
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallUnassign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(
            RoleUserAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignUsersToRoles(
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .userIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).unassignRolesFromUsers(anyList(), anyList());
  }

  @Test
  void testAdministratorRoleDoesNotUnassignInitialUser() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getUsersAssignByRoles(any()))
        .thenReturn(RoleUserAssignment.builder().assignments("1", List.of("1")).build());

    this.useCase.reassignUsersToRoles(
        ReassignUsersToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1"))
            .userIdentifiers(b -> b.ids("3"))
            .build());

    verify(this.roleAssigner, never()).unassignRolesFromUsers(anyList(), anyList());
  }
}
