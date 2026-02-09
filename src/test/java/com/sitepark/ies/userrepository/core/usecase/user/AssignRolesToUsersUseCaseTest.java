package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignRolesToUsersUseCaseTest {

  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private RoleAssigner roleAssigner;
  private UserEntityAuthorizationService userEntityAuthorizationService;

  private AssignRolesToUsersUseCase usecase;

  @BeforeEach
  void setUp() {
    this.userRepository = mock();
    this.roleRepository = mock();
    this.roleAssigner = mock();
    this.userEntityAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());
    this.usecase =
        new AssignRolesToUsersUseCase(
            userRepository,
            roleRepository,
            roleAssigner,
            userEntityAuthorizationService,
            fixedClock);
  }

  @Test
  void testRoleNotWritable() {
    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.assignRolesToUsers(
                AssignRolesToUsersRequest.builder()
                    .userIdentifiers(b -> b.ids("1", "2"))
                    .roleIdentifiers(b -> b.ids("3", "4"))
                    .build()));
  }

  @Test
  void testAssignRolesToUsersCallsRoleAssigner() {

    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(List.of("1", "2"), List.of("3", "4"));
  }

  @Test
  void testAssignRolesToUsersReturnsAssignedResult() {

    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    AssignRolesToUsersResult result =
        this.usecase.assignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(
        result.wasAssigned(),
        "Result should indicate roles were assigned when effective assignments exist");
  }

  @Test
  void testEmptyUserIdentifiersDoesNotCheckAccess() {

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);

    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder().roleIdentifiers(b -> b.ids("3", "4")).build());

    verify(this.userEntityAuthorizationService, never()).isWritable(anyString());
  }

  @Test
  void testEmptyUserIdentifiersReturnsSkipped() {

    AssignRolesToUsersResult result =
        this.usecase.assignRolesToUsers(
            AssignRolesToUsersRequest.builder().roleIdentifiers(b -> b.ids("3", "4")).build());

    assertFalse(
        result.wasAssigned(), "Result should indicate skipped when user identifiers are empty");
  }

  @Test
  void testEmptyRoleIdentifiersDoesNotCheckAccess() {
    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder().userIdentifiers(b -> b.ids("1", "2")).build());

    verify(this.userEntityAuthorizationService, never()).isWritable(anyString());
  }

  @Test
  void testEmptyRoleIdentifiersReturnsSkipped() {
    AssignRolesToUsersResult result =
        this.usecase.assignRolesToUsers(
            AssignRolesToUsersRequest.builder().userIdentifiers(b -> b.ids("1", "2")).build());

    assertFalse(
        result.wasAssigned(), "Result should indicate skipped when role identifiers are empty");
  }

  @Test
  void testResolveUserAnchorCallsRoleAssigner() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.add("anchor"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(List.of("1"), List.of("3", "4"));
  }

  @Test
  void testResolveUserAnchorReturnsAssigned() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    AssignRolesToUsersResult result =
        this.usecase.assignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.add("anchor"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(result.wasAssigned(), "Result should indicate roles were assigned");
  }

  @Test
  void testResolveUserAnchorNotFound() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.assignRolesToUsers(
                AssignRolesToUsersRequest.builder()
                    .userIdentifiers(b -> b.add("anchor"))
                    .roleIdentifiers(b -> b.ids("3", "4"))
                    .build()));
  }

  @Test
  void testResolveRolesAnchorCallsRoleAssigner() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.add("anchor"))
            .build());

    verify(this.roleAssigner).assignRolesToUsers(List.of("1", "2"), List.of("3"));
  }

  @Test
  void testResolveRolesAnchorReturnsAssigned() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().build());

    AssignRolesToUsersResult result =
        this.usecase.assignRolesToUsers(
            AssignRolesToUsersRequest.builder()
                .userIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.add("anchor"))
                .build());

    assertTrue(result.wasAssigned(), "Result should indicate roles were assigned");
  }

  @Test
  void testResolveRoleAnchorNotFound() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.assignRolesToUsers(
                AssignRolesToUsersRequest.builder()
                    .userIdentifiers(b -> b.ids("1", "2"))
                    .roleIdentifiers(b -> b.add("anchor"))
                    .build()));
  }
}
