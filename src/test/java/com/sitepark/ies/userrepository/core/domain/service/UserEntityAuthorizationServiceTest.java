package com.sitepark.ies.userrepository.core.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.sharedkernel.security.FullAccess;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserGrant;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserManagementPermission;
import jakarta.inject.Provider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEntityAuthorizationServiceTest {

  private Provider<Authentication> authenticationProvider;
  private UserEntityAuthorizationService service;

  @BeforeEach
  void setUp() {
    this.authenticationProvider = mock();
    this.service = new UserEntityAuthorizationService(this.authenticationProvider);
  }

  @Test
  void testTypeReturnsUserClass() {

    Class<?> type = this.service.type();

    assertEquals(User.class, type, "type should return User.class");
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
  void testIsCreatableWithUserGrantCreate() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().create(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isCreatable();

    assertTrue(result, "should be creatable with user grant create permission");
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
  void testIsReadableStringWithUserGrantRead() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().read(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isReadable("123");

    assertTrue(result, "should be readable with user grant read permission");
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
  void testIsWritableStringWithUserGrantWrite() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().write(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isWritable("123");

    assertTrue(result, "should be writable with user grant write permission");
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
  void testIsRemovableStringWithUserGrantDelete() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().delete(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isRemovable("123");

    assertTrue(result, "should be removable with user grant delete permission");
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
  void testIsAllowedAssignRoleToUserWithEmptyList() {

    boolean result = this.service.isAllowedAssignRoleToUser(List.of());

    assertTrue(result, "should be allowed to assign empty role list");
  }

  @Test
  void testIsAllowedAssignRoleToUserWithFullAccess() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isAllowedAssignRoleToUser(List.of("role1", "role2"));

    assertTrue(result, "should be allowed to assign roles with full access");
  }

  @Test
  void testIsAllowedAssignRoleToUserWithoutAssignRolesPermission() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().assignRoles(false).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignRoleToUser(List.of("role1"));

    assertFalse(result, "should not be allowed to assign roles without assignRoles permission");
  }

  @Test
  void testIsAllowedAssignRoleToUserWithAssignRolesAndEmptyAllowedList() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().assignRoles(true).allowedRoleIds(List.of()).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignRoleToUser(List.of("role1", "role2"));

    assertTrue(result, "should be allowed to assign any roles with empty allowed role ids");
  }

  @Test
  void testIsAllowedAssignRoleToUserWithAllowedRoleIds() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(
                UserGrant.builder()
                    .assignRoles(true)
                    .allowedRoleIds(List.of("role1", "role2", "role3"))
                    .build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignRoleToUser(List.of("role1", "role2"));

    assertTrue(result, "should be allowed to assign roles that are in allowed list");
  }

  @Test
  void testIsAllowedAssignRoleToUserWithDisallowedRoleIds() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(
                UserGrant.builder()
                    .assignRoles(true)
                    .allowedRoleIds(List.of("role1", "role2"))
                    .build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isAllowedAssignRoleToUser(List.of("role1", "role3"));

    assertFalse(result, "should not be allowed to assign roles not in allowed list");
  }
}
