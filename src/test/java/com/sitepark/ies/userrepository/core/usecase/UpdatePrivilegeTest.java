package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidPermissionException;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePrivilegeTest {
  private PrivilegeRepository repository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;

  private UpdatePrivilege usecase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.accessControl = mock();
    this.roleAssigner = mock();

    this.usecase = new UpdatePrivilege(this.repository, this.roleAssigner, this.accessControl);
  }

  @Test
  void testMissingPermission() {
    Privilege privilege = Privilege.builder().id("1").name("testPrivilege").build();
    assertThrows(
        IllegalArgumentException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {}),
        "Expected IllegalArgumentException for privilege without permissions");
  }

  @Test
  void testAssesDeniedForPrivilege() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(false);
    Privilege privilege =
        Privilege.builder()
            .id("1")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {}),
        "Expected AccessDeniedException for updating privilege without permission");
  }

  @Test
  void testAssesDeniedForRole() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(false);
    Privilege privilege =
        Privilege.builder()
            .id("1")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {"1"}),
        "Expected AccessDeniedException for updating privilege without permission");
  }

  @Test
  void testAnchorNotFound() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.empty());
    Privilege privilege =
        Privilege.builder()
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    assertThrows(
        AnchorNotFoundException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {}),
        "Expected AnchorNotFoundException for non-existing privilege");
  }

  @Test
  void testExistsAnchor() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));
    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {}),
        "Expected AnchorAlreadyExistsException for existing anchor");
  }

  @Test
  void testIdAndAnchorMissing() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    Privilege privilege =
        Privilege.builder().name("testPrivilege").permission(new Permission("test", null)).build();
    assertThrows(
        IllegalArgumentException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {}),
        "Expected IllegalArgumentException for privilege without ID or anchor");
  }

  @Test
  void testInvalidPermission() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    doThrow(new InvalidPermissionException(("Invalid permission")))
        .when(this.repository)
        .validatePermission(any());

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    assertThrows(
        InvalidPermissionException.class,
        () -> this.usecase.updatePrivilege(privilege, new String[] {}),
        "Expected InvalidPermissionException for invalid permission");
  }

  @Test
  void testUpdate() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    Privilege privilege =
        Privilege.builder()
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    this.usecase.updatePrivilege(privilege, new String[] {});

    Privilege expected =
        Privilege.builder()
            .id("2")
            .anchor("anchor")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();

    verify(this.repository).update(expected);
  }

  @Test
  void testAssignPrivilegesToRoles() {
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    Privilege privilege =
        Privilege.builder()
            .id("3")
            .name("testPrivilege")
            .permission(new Permission("test", null))
            .build();
    this.usecase.updatePrivilege(privilege, new String[] {"1", "2"});

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("1", "2"), List.of("3"));
  }
}
