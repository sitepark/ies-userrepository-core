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
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReassignPrivilegesToRolesUseCaseTest {

  private RoleAssigner roleAssigner;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;

  private ReassignPrivilegesToRolesUseCase useCase;

  @BeforeEach
  void setUp() {
    RoleRepository roleRepository = mock();
    PrivilegeRepository privilegeRepository = mock();
    this.roleAssigner = mock();
    this.roleEntityAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.useCase =
        new ReassignPrivilegesToRolesUseCase(
            roleRepository,
            privilegeRepository,
            roleAssigner,
            roleEntityAuthorizationService,
            fixedClock);
  }

  @Test
  void testEmptyRequestReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    ReassignPrivilegesToRolesResult result =
        this.useCase.reassignPrivilegesToRoles(AssignPrivilegesToRolesRequest.builder().build());

    assertFalse(result.wasReassigned(), "Result should indicate skipped when request is empty");
  }

  @Test
  void testNotWritableThrowsAccessDenied() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.reassignPrivilegesToRoles(
                AssignPrivilegesToRolesRequest.builder()
                    .roleIdentifiers(b -> b.ids("1", "2"))
                    .privilegeIdentifiers(b -> b.ids("3", "4"))
                    .build()),
        "Should throw AccessDeniedException when roles are not writable");
  }

  @Test
  void testNoEffectiveChangesReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(
            RolePrivilegeAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    ReassignPrivilegesToRolesResult result =
        this.useCase.reassignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .roleIdentifiers(b -> b.ids("1", "2"))
                .privilegeIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when no effective changes exist");
  }

  @Test
  void testReassignWithNewAssignmentsCallsAssign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(RolePrivilegeAssignment.builder().build());

    this.useCase.reassignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1"))
            .privilegeIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignPrivilegesToRoles(eq(List.of("1")), anyList());
  }

  @Test
  void testReassignWithUnassignmentsCallsUnassign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(RolePrivilegeAssignment.builder().assignments("1", List.of("5", "6")).build());

    this.useCase.reassignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1"))
            .privilegeIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).unassignPrivilegesFromRoles(eq(List.of("1")), anyList());
  }

  @Test
  void testReassignWithEffectiveChangesReturnsReassigned() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(RolePrivilegeAssignment.builder().build());

    ReassignPrivilegesToRolesResult result =
        this.useCase.reassignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .roleIdentifiers(b -> b.ids("1", "2"))
                .privilegeIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(
        result.wasReassigned(),
        "Result should indicate reassignment occurred when effective changes exist");
  }

  @Test
  void testEmptyRoleIdentifiersReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    ReassignPrivilegesToRolesResult result =
        this.useCase.reassignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .privilegeIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when role identifiers are empty");
  }

  @Test
  void testEmptyPrivilegeIdentifiersReturnsSkipped() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(RolePrivilegeAssignment.builder().build());
    ReassignPrivilegesToRolesResult result =
        this.useCase.reassignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder().roleIdentifiers(b -> b.ids("1", "2")).build());

    assertFalse(
        result.wasReassigned(),
        "Result should indicate skipped when privilege identifiers are empty");
  }

  @Test
  void testPartialMatchCreatesEffectiveAssignments() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(
            RolePrivilegeAssignment.builder()
                .assignments("1", List.of("3"))
                .assignments("2", List.of("3"))
                .build());

    this.useCase.reassignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .privilegeIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("1"), List.of("4"));
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallAssign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(
            RolePrivilegeAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .privilegeIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).assignPrivilegesToRoles(anyList(), anyList());
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallUnassign() {
    when(this.roleEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getPrivilegesAssignByRoles(any()))
        .thenReturn(
            RolePrivilegeAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIdentifiers(b -> b.ids("1", "2"))
            .privilegeIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).unassignPrivilegesFromRoles(anyList(), anyList());
  }
}
