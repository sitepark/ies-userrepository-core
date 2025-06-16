package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidPermissionException;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreatePrivilegeTest {
  private PrivilegeRepository repository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;

  private CreatePrivilege usecase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.roleAssigner = mock();
    this.accessControl = mock();

    this.usecase = new CreatePrivilege(repository, roleAssigner, accessControl);
  }

  @Test
  void testWithId() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.usecase.createPrivilege(
                Privilege.builder().id("2").name("name").build(), new String[] {}),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testWithoutPermission() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.usecase.createPrivilege(Privilege.builder().name("name").build(), new String[] {}),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testPermissionNotCreatable() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.createPrivilege(
                Privilege.builder().name("name").permission(new Permission("type", null)).build(),
                new String[] {}),
        "Expected AccessDeniedException for privilege creation");
  }

  @Test
  void testRoleNotWritable() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.usecase.createPrivilege(
                Privilege.builder().name("name").permission(new Permission("type", null)).build(),
                new String[] {"1"}),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testWithExistsAnchor() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    assertThrows(
        AnchorAlreadyExistsException.class,
        () ->
            this.usecase.createPrivilege(
                Privilege.builder()
                    .name("name")
                    .anchor("anchor")
                    .permission(new Permission("type", null))
                    .build(),
                new String[] {}),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  void testWithInvalidPermission() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    doThrow(new InvalidPermissionException("Invalid permission"))
        .when(this.repository)
        .validatePermission(any());

    assertThrows(
        InvalidPermissionException.class,
        () ->
            this.usecase.createPrivilege(
                Privilege.builder()
                    .name("name")
                    .anchor("anchor")
                    .permission(new Permission("type", null))
                    .build(),
                new String[] {}),
        "Expected IllegalArgumentException for privilege with ID");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testCreate() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn("456");

    String id =
        this.usecase.createPrivilege(
            Privilege.builder()
                .name("name")
                .anchor("anchor")
                .permission(new Permission("type", null))
                .build(),
            new String[] {});

    verify(this.repository).create(any());
    assertEquals("456", id, "Expected ID to be returned after creation");
  }

  @Test
  void testWithAssignPrivilegesToRoles() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.create(any())).thenReturn("456");

    this.usecase.createPrivilege(
        Privilege.builder()
            .name("name")
            .anchor("anchor")
            .permission(new Permission("type", null))
            .build(),
        new String[] {"1"});

    verify(this.roleAssigner).assignPrivilegesToRoles(List.of("1"), List.of("456"));
  }
}
