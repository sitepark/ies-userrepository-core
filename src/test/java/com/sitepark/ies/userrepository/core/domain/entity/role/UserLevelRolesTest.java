package com.sitepark.ies.userrepository.core.domain.entity.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import org.junit.jupiter.api.Test;

class UserLevelRolesTest {

  @Test
  void testIsUserLevelRoleAdministrator() {
    assertTrue(
        UserLevelRoles.isUserLevelRole(Role.ofName("ADMINISTRATOR")),
        "ADMINISTRATOR should be user level role");
  }

  @Test
  void testIsUserLevelRoleUser() {
    assertTrue(
        UserLevelRoles.isUserLevelRole(Role.ofName("USER")), "USER should be user level role");
  }

  @Test
  void testIsUserLevelRoleExternal() {
    assertTrue(
        UserLevelRoles.isUserLevelRole(Role.ofName("EXTERNAL")),
        "EXTERNAL should be user level role");
  }

  @Test
  void testIsNotUserLevelRole() {
    assertFalse(
        UserLevelRoles.isUserLevelRole(Role.ofName("ABC")), "ABC should't be user level role");
  }

  @Test
  void testvalueOfAdministrator() {
    assertEquals(
        UserLevelRoles.ADMINISTRATOR, UserLevelRoles.valueOf("ADMINISTRATOR"), "unexpected role");
  }

  @Test
  void testvalueOfUser() {
    assertEquals(UserLevelRoles.USER, UserLevelRoles.valueOf("USER"), "unexpected role");
  }

  @Test
  void testvalueOfExternal() {
    assertEquals(UserLevelRoles.EXTERNAL, UserLevelRoles.valueOf("EXTERNAL"), "unexpected role");
  }
}
