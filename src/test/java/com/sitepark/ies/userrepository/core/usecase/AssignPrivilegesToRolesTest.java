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
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignPrivilegesToRolesTest {

  private RoleRepository roleRepository;
  private PrivilegeRepository privilegeRepository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;

  private AssignPrivilegesToRoles usecase;

  @BeforeEach
  void setUp() {
    this.roleRepository = mock();
    this.privilegeRepository = mock();
    this.roleAssigner = mock();
    this.accessControl = mock();

    this.usecase =
        new AssignPrivilegesToRoles(
            roleRepository, privilegeRepository, roleAssigner, accessControl);
  }

  @Test
  void testRoleNotWritable() {
    when(this.accessControl.isRoleWritable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.assignPrivilegesToRoles(
                AssignPrivilegesToRolesRequest.builder()
                    .roleIds("1", "2")
                    .privilegeIds("3", "4")
                    .build()));
  }

  @Test
  void testAssignPrivilegesToRoles() {

    when(this.accessControl.isRoleWritable()).thenReturn(true);

    this.usecase.assignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder().roleIds("1", "2").privilegeIds("3", "4").build());

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("1", "2"), List.of("3", "4"));
  }

  @Test
  void testEmptyRoleIdentifiers() {

    when(this.accessControl.isRoleWritable()).thenReturn(true);

    this.usecase.assignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder().privilegeIds("3", "4").build());

    verify(this.accessControl, never()).isRoleWritable();
  }

  @Test
  void testEmptyPrivilegeIdentifiers() {
    this.usecase.assignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder().roleIds("1", "2").build());

    verify(this.accessControl, never()).isRoleWritable();
  }

  @Test
  void testResolveRoleAnchor() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    this.usecase.assignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIdentifier(Identifier.ofAnchor("anchor"))
            .privilegeIds("3", "4")
            .build());

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("1"), List.of("3", "4"));
  }

  @Test
  void testResolveRoleAnchorNotFound() {
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.assignPrivilegesToRoles(
                AssignPrivilegesToRolesRequest.builder()
                    .roleIdentifier(Identifier.ofAnchor("anchor"))
                    .privilegeIds("3", "4")
                    .build()));
  }

  @Test
  void testResolvePrivilegeAnchor() {
    when(this.privilegeRepository.resolveAnchor(any())).thenReturn(Optional.of("3"));
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    this.usecase.assignPrivilegesToRoles(
        AssignPrivilegesToRolesRequest.builder()
            .roleIds("1", "2")
            .privilegeIdentifier(Identifier.ofAnchor("anchor"))
            .build());

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("1", "2"), List.of("3"));
  }

  @Test
  void testResolvePrivilegeAnchorNotFound() {
    when(this.privilegeRepository.resolveAnchor(any())).thenReturn(Optional.empty());
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.assignPrivilegesToRoles(
                AssignPrivilegesToRolesRequest.builder()
                    .roleIds("1", "2")
                    .privilegeIdentifier(Identifier.ofAnchor("anchor"))
                    .build()));
  }
}
