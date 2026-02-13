package com.sitepark.ies.userrepository.core.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.sharedkernel.security.FullAccess;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.value.permission.RoleGrant;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserManagementPermission;
import jakarta.inject.Provider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleEntityAuthorizationServiceTest {

  private Provider<Authentication> authenticationProvider;
  private RoleEntityAuthorizationService service;

  @BeforeEach
  void setUp() {
    this.authenticationProvider = mock();
    this.service = new RoleEntityAuthorizationService(this.authenticationProvider);
  }

  @Test
  void testTypeReturnsRoleClass() {

    Class<?> type = this.service.type();

    assertEquals(Role.class, type, "type should return Role.class");
  }

  @Test
  void testIsCreatableWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isCreatable();

    assertTrue(result, "should be creatable with full access");
  }

  @Test
  void testIsCreatableWithRoleGrantCreate() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(RoleGrant.builder().create(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isCreatable();

    assertTrue(result, "should be creatable with role grant create permission");
  }

  @Test
  void testIsCreatableWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isCreatable();

    assertFalse(result, "should not be creatable without permission");
  }

  @Test
  void testIsCreatableWithNullAuthentication() {

    when(this.authenticationProvider.get()).thenReturn(null);

    boolean result = this.service.isCreatable();

    assertFalse(result, "should not be creatable with null authentication");
  }

  @Test
  void testIsReadableStringWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isReadable("123");

    assertTrue(result, "should be readable with full access");
  }

  @Test
  void testIsReadableStringWithRoleGrantRead() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(RoleGrant.builder().read(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isReadable("123");

    assertTrue(result, "should be readable with role grant read permission");
  }

  @Test
  void testIsReadableStringWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isReadable("123");

    assertFalse(result, "should not be readable without permission");
  }

  @Test
  void testIsReadableListWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isReadable(List.of("123", "456"));

    assertTrue(result, "should be readable with full access");
  }

  @Test
  void testIsReadableListWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isReadable(List.of("123", "456"));

    assertFalse(result, "should not be readable without permission");
  }

  @Test
  void testIsWritableStringWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isWritable("123");

    assertTrue(result, "should be writable with full access");
  }

  @Test
  void testIsWritableStringWithRoleGrantWrite() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(RoleGrant.builder().write(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isWritable("123");

    assertTrue(result, "should be writable with role grant write permission");
  }

  @Test
  void testIsWritableStringWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isWritable("123");

    assertFalse(result, "should not be writable without permission");
  }

  @Test
  void testIsWritableListWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isWritable(List.of("123", "456"));

    assertTrue(result, "should be writable with full access");
  }

  @Test
  void testIsWritableListWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isWritable(List.of("123", "456"));

    assertFalse(result, "should not be writable without permission");
  }

  @Test
  void testIsRemovableStringWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isRemovable("123");

    assertTrue(result, "should be removable with full access");
  }

  @Test
  void testIsRemovableStringWithRoleGrantDelete() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(RoleGrant.builder().delete(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isRemovable("123");

    assertTrue(result, "should be removable with role grant delete permission");
  }

  @Test
  void testIsRemovableStringWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isRemovable("123");

    assertFalse(result, "should not be removable without permission");
  }

  @Test
  void testIsRemovableListWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isRemovable(List.of("123", "456"));

    assertTrue(result, "should be removable with full access");
  }

  @Test
  void testIsRemovableListWithoutPermission() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class)).thenReturn(Optional.empty());

    boolean result = this.service.isRemovable(List.of("123", "456"));

    assertFalse(result, "should not be removable without permission");
  }

  @Test
  void testIsAllowedAssignPrivilegesToRoleWithEmptyList() {

    boolean result = this.service.isAllowedAssignPrivilegesToRole(List.of());

    assertTrue(result, "should be allowed to assign empty privilege list");
  }

  @Test
  void testIsAllowedAssignPrivilegesToRoleWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isAllowedAssignPrivilegesToRole(List.of("priv1", "priv2"));

    assertTrue(result, "should be allowed to assign privileges with full access");
  }

  @Test
  void testIsAllowedAssignPrivilegesToRoleWithoutAssignPrivilegesPermission() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(RoleGrant.builder().assignPrivileges(false).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignPrivilegesToRole(List.of("priv1"));

    assertFalse(
        result, "should not be allowed to assign privileges without assignPrivileges permission");
  }

  @Test
  void testIsAllowedAssignPrivilegesToRoleWithAssignPrivilegesAndEmptyAllowedList() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(
                RoleGrant.builder().assignPrivileges(true).allowedPrivilegeIds(List.of()).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignPrivilegesToRole(List.of("priv1", "priv2"));

    assertTrue(
        result, "should be allowed to assign any privileges with empty allowed privilege ids");
  }

  @Test
  void testIsAllowedAssignPrivilegesToRoleWithAllowedPrivilegeIds() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(
                RoleGrant.builder()
                    .assignPrivileges(true)
                    .allowedPrivilegeIds(List.of("priv1", "priv2", "priv3"))
                    .build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignPrivilegesToRole(List.of("priv1", "priv2"));

    assertTrue(result, "should be allowed to assign privileges that are in allowed list");
  }

  @Test
  void testIsAllowedAssignPrivilegesToRoleWithDisallowedPrivilegeIds() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .roleGrant(
                RoleGrant.builder()
                    .assignPrivileges(true)
                    .allowedPrivilegeIds(List.of("priv1", "priv2"))
                    .build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignPrivilegesToRole(List.of("priv1", "priv3"));

    assertFalse(result, "should not be allowed to assign privileges not in allowed list");
  }
}
