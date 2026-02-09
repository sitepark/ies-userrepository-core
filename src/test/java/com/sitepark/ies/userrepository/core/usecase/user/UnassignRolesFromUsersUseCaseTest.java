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

class UnassignRolesFromUsersUseCaseTest {

  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private RoleAssigner roleAssigner;
  private UserEntityAuthorizationService userEntityAuthorizationService;

  private UnassignRolesFromUsersUseCase usecase;

  @BeforeEach
  void setUp() {
    this.userRepository = mock();
    this.roleRepository = mock();
    this.roleAssigner = mock();
    this.userEntityAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.usecase =
        new UnassignRolesFromUsersUseCase(
            userRepository,
            roleRepository,
            roleAssigner,
            userEntityAuthorizationService,
            fixedClock);
  }

  @Test
  void testUserNotWritable() {
    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.unassignRolesFromUsers(
                UnassignRolesFromUsersRequest.builder()
                    .userIdentifiers(b -> b.ids("1", "2"))
                    .roleIdentifiers(b -> b.ids("3", "4"))
                    .build()));
  }

  @Test
  void testUnassignRolesToUsersCallsRoleAssigner() {

    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    this.usecase.unassignRolesFromUsers(
        UnassignRolesFromUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).unassignRolesFromUsers(List.of("1", "2"), List.of("3", "4"));
  }

  @Test
  void testUnassignRolesToUsersReturnsUnassignedResult() {

    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3", "4"))
                .assignments("2", List.of("3", "4"))
                .build());

    UnassignRolesFromUsersResult result =
        this.usecase.unassignRolesFromUsers(
            UnassignRolesFromUsersRequest.builder()
                .userIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(
        result.wasUnassigned(),
        "Result should indicate roles were unassigned when effective unassignments exist");
  }

  @Test
  void testEmptyUsersIdentifiersDoesNotCheckAccess() {

    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);

    this.usecase.unassignRolesFromUsers(
        UnassignRolesFromUsersRequest.builder().roleIdentifiers(b -> b.ids("3", "4")).build());

    verify(this.userEntityAuthorizationService, never()).isWritable(anyString());
  }

  @Test
  void testEmptyUsersIdentifiersReturnsSkipped() {

    UnassignRolesFromUsersResult result =
        this.usecase.unassignRolesFromUsers(
            UnassignRolesFromUsersRequest.builder().roleIdentifiers(b -> b.ids("3", "4")).build());

    assertFalse(
        result.wasUnassigned(), "Result should indicate skipped when user identifiers are empty");
  }

  @Test
  void testEmptyRolesIdentifiersDoesNotCheckAccess() {
    this.usecase.unassignRolesFromUsers(
        UnassignRolesFromUsersRequest.builder().userIdentifiers(b -> b.ids("1", "2")).build());

    verify(this.userEntityAuthorizationService, never()).isWritable(anyString());
  }

  @Test
  void testEmptyRolesIdentifiersReturnsSkipped() {
    UnassignRolesFromUsersResult result =
        this.usecase.unassignRolesFromUsers(
            UnassignRolesFromUsersRequest.builder().userIdentifiers(b -> b.ids("1", "2")).build());

    assertFalse(
        result.wasUnassigned(), "Result should indicate skipped when role identifiers are empty");
  }

  @Test
  void testResolveUserAnchorCallsRoleAssigner() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().assignments("1", List.of("3", "4")).build());

    this.usecase.unassignRolesFromUsers(
        UnassignRolesFromUsersRequest.builder()
            .userIdentifiers(b -> b.add("anchor"))
            .roleIdentifiers(b -> b.ids("3", "4"))
            .build());

    verify(this.roleAssigner).unassignRolesFromUsers(List.of("1"), List.of("3", "4"));
  }

  @Test
  void testResolveUserAnchorReturnsUnassigned() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(UserRoleAssignment.builder().assignments("1", List.of("3", "4")).build());

    UnassignRolesFromUsersResult result =
        this.usecase.unassignRolesFromUsers(
            UnassignRolesFromUsersRequest.builder()
                .userIdentifiers(b -> b.add("anchor"))
                .roleIdentifiers(b -> b.ids("3", "4"))
                .build());

    assertTrue(result.wasUnassigned(), "Result should indicate roles were unassigned");
  }

  @Test
  void testResolveUserAnchorNotFound() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.unassignRolesFromUsers(
                UnassignRolesFromUsersRequest.builder()
                    .userIdentifiers(b -> b.add("anchor"))
                    .roleIdentifiers(b -> b.ids("3", "4"))
                    .build()));
  }

  @Test
  void testResolveRoleAnchorCallsRoleAssigner() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3"))
                .assignments("2", List.of("3"))
                .build());

    this.usecase.unassignRolesFromUsers(
        UnassignRolesFromUsersRequest.builder()
            .userIdentifiers(b -> b.ids("1", "2"))
            .roleIdentifiers(b -> b.add("anchor"))
            .build());

    verify(this.roleAssigner).unassignRolesFromUsers(List.of("1", "2"), List.of("3"));
  }

  @Test
  void testResolveRoleAnchorReturnsUnassigned() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.userEntityAuthorizationService.isWritable(anyList())).thenReturn(true);
    when(this.roleAssigner.getRolesAssignByUsers(any()))
        .thenReturn(
            UserRoleAssignment.builder()
                .assignments("1", List.of("3"))
                .assignments("2", List.of("3"))
                .build());

    UnassignRolesFromUsersResult result =
        this.usecase.unassignRolesFromUsers(
            UnassignRolesFromUsersRequest.builder()
                .userIdentifiers(b -> b.ids("1", "2"))
                .roleIdentifiers(b -> b.add("anchor"))
                .build());

    assertTrue(result.wasUnassigned(), "Result should indicate roles were unassigned");
  }

  @Test
  void testResolveRoleAnchorNotFound() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.userEntityAuthorizationService.isWritable(anyString())).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.unassignRolesFromUsers(
                UnassignRolesFromUsersRequest.builder()
                    .userIdentifiers(b -> b.ids("1", "2"))
                    .roleIdentifiers(b -> b.add("anchor"))
                    .build()));
  }
}
