package com.sitepark.ies.userrepository.core.usecase.privilege;

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
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeRoleAssignment;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReassignRolesToPrivilegesUseCaseTest {

  private RoleAssigner roleAssigner;
  private PrivilegeEntityAuthorizationService privilegeEntityAuthorizationService;

  private ReassignRolesToPrivilegesUseCase useCase;

  @BeforeEach
  void setUp() {
    RoleRepository roleRepository = mock();
    PrivilegeRepository privilegeRepository = mock();
    this.roleAssigner = mock();
    this.privilegeEntityAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.useCase =
        new ReassignRolesToPrivilegesUseCase(
            roleRepository,
            privilegeRepository,
            roleAssigner,
            privilegeEntityAuthorizationService,
            fixedClock);
  }

  @Test
  void testEmptyRequestReturnsSkipped() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    ReassignRolesToPrivilegesResult result =
        this.useCase.reassignRolesToPrivileges(ReassignRolesToPrivilegesRequest.builder().build());

    assertFalse(result.wasReassigned(), "Result should indicate skipped when request is empty");
  }

  @Test
  void testNotWritableThrowsAccessDenied() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(false);

    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.reassignRolesToPrivileges(
                ReassignRolesToPrivilegesRequest.builder()
                    .privilegeIdentifiers(b -> b.ids("1", "2"))
                    .roleIdentifiers(b -> b.ids("3", "4"))
                    .build()),
        "Should throw AccessDeniedException when privileges are not writable");
  }

  @Test
  void testNoEffectiveChangesReturnsSkipped() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(
            PrivilegeRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    ReassignRolesToPrivilegesResult result =
        this.useCase.reassignRolesToPrivileges(
            ReassignRolesToPrivilegesRequest.builder()
                .privilegeIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when no effective changes exist");
  }

  @Test
  void testReassignWithNewAssignmentsCallsAssign() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(PrivilegeRoleAssignment.builder().build());

    this.useCase.reassignRolesToPrivileges(
        ReassignRolesToPrivilegesRequest.builder()
            .privilegeIdentifiers(b -> b.ids("1"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignPrivilegesToRoles(anyList(), eq(List.of("1")));
  }

  @Test
  void testReassignWithUnassignmentsCallsUnassign() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(PrivilegeRoleAssignment.builder().assignments("1", List.of("5", "6")).build());

    this.useCase.reassignRolesToPrivileges(
        ReassignRolesToPrivilegesRequest.builder()
            .privilegeIdentifiers(b -> b.ids("1"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).unassignPrivilegesFromRoles(anyList(), eq(List.of("1")));
  }

  @Test
  void testReassignWithEffectiveChangesReturnsReassigned() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(PrivilegeRoleAssignment.builder().build());

    ReassignRolesToPrivilegesResult result =
        this.useCase.reassignRolesToPrivileges(
            ReassignRolesToPrivilegesRequest.builder()
                .privilegeIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(
        result.wasReassigned(),
        "Result should indicate reassignment occurred when effective changes exist");
  }

  @Test
  void testEmptyPrivilegeIdentifiersReturnsSkipped() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    ReassignRolesToPrivilegesResult result =
        this.useCase.reassignRolesToPrivileges(
            ReassignRolesToPrivilegesRequest.builder()
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertFalse(
        result.wasReassigned(),
        "Result should indicate skipped when privilege identifiers are empty");
  }

  @Test
  void testEmptyRoleIdentifiersReturnsSkipped() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(PrivilegeRoleAssignment.builder().build());
    ReassignRolesToPrivilegesResult result =
        this.useCase.reassignRolesToPrivileges(
            ReassignRolesToPrivilegesRequest.builder()
                .privilegeIdentifiers(b -> b.ids("1", "2"))
                .build());

    assertFalse(
        result.wasReassigned(), "Result should indicate skipped when role identifiers are empty");
  }

  @Test
  void testPartialMatchCreatesEffectiveAssignments() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(
            PrivilegeRoleAssignment.builder()
                .assignments("1", List.of("3"))
                .assignments("2", List.of("3"))
                .build());

    this.useCase.reassignRolesToPrivileges(
        ReassignRolesToPrivilegesRequest.builder()
            .privilegeIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("4"), List.of("1"));
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallAssign() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(
            PrivilegeRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignRolesToPrivileges(
        ReassignRolesToPrivilegesRequest.builder()
            .privilegeIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).assignPrivilegesToRoles(anyList(), anyList());
  }

  @Test
  void testWithoutEffectiveChangesDoesNotCallUnassign() {
    when(this.privilegeEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByPrivileges(any()))
        .thenReturn(
            PrivilegeRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.useCase.reassignRolesToPrivileges(
        ReassignRolesToPrivilegesRequest.builder()
            .privilegeIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner, never()).unassignPrivilegesFromRoles(anyList(), anyList());
  }
}
