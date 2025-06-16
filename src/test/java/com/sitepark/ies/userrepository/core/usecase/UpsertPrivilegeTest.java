package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertPrivilegeTest {

  private PrivilegeRepository repository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;

  private UpsertPrivilege usecase;

  public @BeforeEach void setUp() {
    this.repository = mock();
    this.accessControl = mock();
    this.roleAssigner = mock();

    this.usecase = new UpsertPrivilege(this.repository, this.roleAssigner, this.accessControl);
  }

  @Test
  void testPermissionNotSet() {
    Privilege privilege = Privilege.builder().name("test").build();
    assertThrows(
        IllegalArgumentException.class,
        () -> this.usecase.upsertPrivilege(privilege, new String[] {}),
        "Expected IllegalArgumentException for privilege without permissions");
  }

  @Test
  void testRoleWritable() {
    Privilege privilege =
        Privilege.builder().name("test").permission(new Permission("test", null)).build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.upsertPrivilege(privilege, new String[] {"1"}),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testCreatablePrivilege() {
    Privilege privilege =
        Privilege.builder().name("test").permission(new Permission("test", null)).build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.upsertPrivilege(privilege, new String[] {}),
        "Expected AccessDeniedException for creating privilege without permission");
  }

  @Test
  void testCreatePrivilege() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    Privilege privilege =
        Privilege.builder().name("test").permission(new Permission("test", null)).build();
    this.usecase.upsertPrivilege(privilege, new String[] {});

    verify(this.repository).create(privilege);
  }

  @Test
  void testAssignRolesForCreatedPrivilege() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn("1");
    Privilege privilege =
        Privilege.builder().name("test").permission(new Permission("test", null)).build();
    this.usecase.upsertPrivilege(privilege, new String[] {"2"});

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("2"), List.of("1"));
  }

  @Test
  void testWritablePrivilege() {
    Privilege privilege =
        Privilege.builder().id("1").name("test").permission(new Permission("test", null)).build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.upsertPrivilege(privilege, new String[] {}),
        "Expected AccessDeniedException for creating privilege without permission");
  }

  @Test
  void testUpdatePrivilege() {

    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    Privilege privilege =
        Privilege.builder().id("1").name("test").permission(new Permission("test", null)).build();
    this.usecase.upsertPrivilege(privilege, new String[] {});

    verify(this.repository).update(privilege);
  }

  @Test
  void testUpdatePrivilegeWithAnchor() {

    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    Privilege privilege =
        Privilege.builder()
            .anchor("anchor")
            .name("test")
            .permission(new Permission("test", null))
            .build();
    this.usecase.upsertPrivilege(privilege, new String[] {});

    Privilege expected = privilege.toBuilder().id("1").build();

    verify(this.repository).update(expected);
  }

  @Test
  void testUpdatePrivilegeWithExistsAnchor() {

    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("test")
            .permission(new Permission("test", null))
            .build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.usecase.upsertPrivilege(privilege, new String[] {}),
        "Expected AnchorAlreadyExistsException for privilege with existing anchor");
  }

  @Test
  void testUpdatePrivilegeWithUnchangedAnchor() {

    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("test")
            .permission(new Permission("test", null))
            .build();

    this.usecase.upsertPrivilege(privilege, new String[] {});

    verify(this.repository).update(privilege);
  }

  @Test
  void testAssignRolesForUpdatePrivilege() {

    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    Privilege privilege =
        Privilege.builder().id("1").name("test").permission(new Permission("test", null)).build();
    this.usecase.upsertPrivilege(privilege, new String[] {"2"});

    verify(this.roleAssigner).reassignPrivilegesToRoles(List.of("2"), List.of("1"));
  }
}
