package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignRolesToUsersTest {

  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;

  private AssignRolesToUsers usecase;

  @BeforeEach
  void setUp() {
    this.userRepository = mock();
    this.roleRepository = mock();
    this.roleAssigner = mock();
    this.accessControl = mock();

    this.usecase =
        new AssignRolesToUsers(userRepository, roleRepository, roleAssigner, accessControl);
  }

  @Test
  void testRoleNotWritable() {
    when(this.accessControl.isUserWritable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.assignRolesToUsers(
                List.of(Identifier.ofId("1"), Identifier.ofId("2")),
                List.of(Identifier.ofId("3"), Identifier.ofId("4"))));
  }

  @Test
  void testAssignRolesToUsers() {

    when(this.accessControl.isUserWritable()).thenReturn(true);

    this.usecase.assignRolesToUsers(
        List.of(Identifier.ofId("1"), Identifier.ofId("2")),
        List.of(Identifier.ofId("3"), Identifier.ofId("4")));

    verify(this.roleAssigner).assignRolesToUsers(List.of("1", "2"), List.of("3", "4"));
  }

  @Test
  void testEmptyUserIdentifiers() {

    when(this.accessControl.isUserWritable()).thenReturn(true);

    this.usecase.assignRolesToUsers(List.of(), List.of(Identifier.ofId("3"), Identifier.ofId("4")));

    verify(this.accessControl, never()).isUserWritable();
  }

  @Test
  void testEmptyRoleIdentifiers() {
    this.usecase.assignRolesToUsers(List.of(Identifier.ofId("1"), Identifier.ofId("2")), List.of());

    verify(this.accessControl, never()).isUserWritable();
  }

  @Test
  void testResolveUserAnchor() {
    when(this.userRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.accessControl.isUserWritable()).thenReturn(true);

    this.usecase.assignRolesToUsers(
        List.of(Identifier.ofAnchor("anchor")),
        List.of(Identifier.ofId("3"), Identifier.ofId("4")));

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
                List.of(Identifier.ofAnchor("anchor")),
                List.of(Identifier.ofId("3"), Identifier.ofId("4"))));
  }

  @Test
  void testResolveRolesAnchor() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.accessControl.isUserWritable()).thenReturn(true);

    this.usecase.assignRolesToUsers(
        List.of(Identifier.ofId("1"), Identifier.ofId("2")),
        List.of(Identifier.ofAnchor("anchor")));

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
                List.of(Identifier.ofId("1"), Identifier.ofId("2")),
                List.of(Identifier.ofAnchor("anchor"))));
  }
}
