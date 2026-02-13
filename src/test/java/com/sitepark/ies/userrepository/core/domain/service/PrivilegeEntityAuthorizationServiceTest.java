package com.sitepark.ies.userrepository.core.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.sharedkernel.security.FullAccess;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.permission.PrivilegeGrant;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserManagementPermission;
import jakarta.inject.Provider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrivilegeEntityAuthorizationServiceTest {

  private Provider<Authentication> authenticationProvider;
  private PrivilegeEntityAuthorizationService service;

  @BeforeEach
  void setUp() {
    this.authenticationProvider = mock();
    this.service = new PrivilegeEntityAuthorizationService(this.authenticationProvider);
  }

  @Test
  void testTypeReturnsPrivilegeClass() {

    Class<?> type = this.service.type();

    assertEquals(Privilege.class, type, "type should return Privilege.class");
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
  void testIsCreatableWithPrivilegeGrantCreate() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .privilegeGrant(PrivilegeGrant.builder().create(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isCreatable();

    assertTrue(result, "should be creatable with privilege grant create permission");
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
  void testIsReadableStringWithPrivilegeGrantRead() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .privilegeGrant(PrivilegeGrant.builder().read(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isReadable("123");

    assertTrue(result, "should be readable with privilege grant read permission");
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
  void testIsWritableStringWithPrivilegeGrantWrite() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .privilegeGrant(PrivilegeGrant.builder().write(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isWritable("123");

    assertTrue(result, "should be writable with privilege grant write permission");
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
  void testIsRemovableStringWithPrivilegeGrantDelete() {

    Authentication authentication = mock();
    UserManagementPermission permission =
        UserManagementPermission.builder()
            .privilegeGrant(PrivilegeGrant.builder().delete(true).build())
            .build();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(false);
    when(authentication.getPermission(UserManagementPermission.class))
        .thenReturn(Optional.of(permission));

    boolean result = this.service.isRemovable("123");

    assertTrue(result, "should be removable with privilege grant delete permission");
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
  void testIsRemovableListAlwaysReturnsFalse() {

    Authentication authentication = mock();
    when(this.authenticationProvider.get()).thenReturn(authentication);
    when(authentication.hasPermission(FullAccess.class)).thenReturn(true);

    boolean result = this.service.isRemovable(List.of("123", "456"));

    assertFalse(result, "should always return false for list variant");
  }

  @Test
  void testIsRemovableListWithoutPermission() {

    when(this.authenticationProvider.get()).thenReturn(null);

    boolean result = this.service.isRemovable(List.of("123", "456"));

    assertFalse(result, "should always return false for list variant");
  }
}
