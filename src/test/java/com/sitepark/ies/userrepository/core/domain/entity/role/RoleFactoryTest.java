package com.sitepark.ies.userrepository.core.domain.entity.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAnchorException;
import org.junit.jupiter.api.Test;

class RoleFactoryTest {

  @Test
  void testCreateWithUserLEvel() {
    RoleFactory factory = new RoleFactory();
    Role role = factory.create("USER");
    assertEquals(UserLevelRoles.USER, role, "unexpected role");
  }

  @Test
  void testCreateWithId() {
    RoleFactory factory = new RoleFactory();
    Ref ref = (Ref) factory.create("123");
    assertEquals("123", ref.getId().get(), "unexpected id");
    assertTrue(ref.getAnchor().isEmpty(), "anchor should be empty");
  }

  @Test
  void testCreateWithAnchor() {
    RoleFactory factory = new RoleFactory();
    Ref ref = (Ref) factory.create("group.user");
    assertEquals("group.user", ref.getAnchor().get().getName(), "unexpected anchor");
    assertTrue(ref.getId().isEmpty(), "id should be empty");
  }

  @Test
  void testCreateInvalid() {
    RoleFactory factory = new RoleFactory();
    assertThrows(
        InvalidAnchorException.class,
        () -> {
          factory.create("a/g");
        });
  }
}
