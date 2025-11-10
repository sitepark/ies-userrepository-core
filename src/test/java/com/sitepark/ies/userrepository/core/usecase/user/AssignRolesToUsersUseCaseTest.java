package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
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
  private AccessControl accessControl;

  private AssignRolesToUsersUseCase usecase;

  @BeforeEach
  void setUp() {
    this.userRepository = mock();
    this.roleRepository = mock();
    this.roleAssigner = mock();
    this.accessControl = mock();

    AuditLogService auditLogService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());
    this.usecase =
        new AssignRolesToUsersUseCase(
            userRepository,
            roleRepository,
            roleAssigner,
            accessControl,
            auditLogService,
            fixedClock);
  }

  @Test
  void testRoleNotWritable() {
    when(this.accessControl.isUserWritable()).thenReturn(false);
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
  void testAssignRolesToUsers() {

    when(this.accessControl.isUserWritable()).thenReturn(true);
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
  void testEmptyUserIdentifiers() {

    when(this.accessControl.isUserWritable()).thenReturn(true);

    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder().roleIdentifiers(b -> b.ids("3", "4")).build());

    verify(this.accessControl, never()).isUserWritable();
  }

  @Test
  void testEmptyRoleIdentifiers() {
    this.usecase.assignRolesToUsers(
        AssignRolesToUsersRequest.builder().userIdentifiers(b -> b.ids("1", "2")).build());

    verify(this.accessControl, never()).isUserWritable();
  }

  @Test
  void testResolveUserAnchor() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.accessControl.isUserWritable()).thenReturn(true);
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
  void testResolveUserAnchorNotFound() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.accessControl.isUserWritable()).thenReturn(true);

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
  void testResolveRolesAnchor() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.accessControl.isUserWritable()).thenReturn(true);
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
  void testResolveRoleAnchorNotFound() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.accessControl.isUserWritable()).thenReturn(true);

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
