package com.sitepark.ies.userrepository.core.usecase.user;

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
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReassignRolesToUsersUseCaseTest {

  private RoleAssigner roleAssigner;
  private UserEntityAuthorizationService userEntityAuthorizationService;

  private ReassignRolesToUsersUseCase useCase;

  @BeforeEach
  void setUp() {
    UserRepository userRepository = mock();
    RoleRepository roleRepository = mock();
    this.roleAssigner = mock();
    this.userEntityAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.useCase =
        new ReassignRolesToUsersUseCase(
            userRepository,
            roleRepository,
            roleAssigner,
            userEntityAuthorizationService,
            fixedClock);
  }

  @Test
  void testEmptyRequestReturnsSkipped() {
    ReassignRolesToUsersResult result =
        this.useCase.reassignRolesToUsers(AssignRolesToUsersRequest.builder().build());

    assertFalse(result.wasReassigned(), "Result should indicate skipped when request is empty");
  }

  @Test
  void testNotWritableThrowsAccessDenied() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.reassignRolesToUsers(
                AssignRolesToUsersRequest.builder()
                    .userIdentifiers(b -> b.ids("1", "2"))
                    .roleIdentifiers(b -> b.ids("3", "4"))
                    .build()),
        "Should throw AccessDeniedException when users are not writable");
  }

  @Test
  void testNoEffectiveChangesReturnsSkipped() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    ReassignRolesToUsersResult result =
        this.useCase.reassignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when no effective changes exist");
  }

  @Test
  void testReassignWithNewAssignmentsCallsAssign() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    this.useCase.reassignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(eq(List.of("1")), anyList());
  }

  @Test
  void testReassignWithUnassignmentsCallsUnassign() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().assignments("1", List.of("5", "6")).build());

    this.useCase.reassignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).unassignRolesFromUsers(eq(List.of("1")), anyList());
  }

  @Test
  void testReassignWithEffectiveChangesReturnsReassigned() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    ReassignRolesToUsersResult result =
        this.useCase.reassignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(
        result.wasReassigned(),
        "Result should indicate reassignment occurred when effective changes exist");
  }

  @Test
  void testEmptyUserIdentifiersReturnsSkipped() {
    ReassignRolesToUsersResult result =
        this.useCase.reassignRolesToUsers(
            AssignRolesToUsersRequest.builder().roleIdentifiers(b -> b.ids("3", "4")).build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when user identifiers are empty");
  }

  @Test
  void testEmptyRoleIdentifiersReturnsSkipped() {
    ReassignRolesToUsersResult result =
        this.useCase.reassignRolesToUsers(
            AssignRolesToUsersRequest.builder().userIdentifiers(b -> b.ids("1", "2")).build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when role identifiers are empty");
  }

  @Test
  void testPartialMatchCreatesEffectiveAssignments() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3"))
                .assignments("2", List.of("3"))
                .build());

    this.useCase.reassignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(List.of("1"), List.of("4"));
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallAssign() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).assignRolesToUsers(anyList(), anyList());
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallUnassign() {
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).unassignRolesFromUsers(anyList(), anyList());
  }
}
