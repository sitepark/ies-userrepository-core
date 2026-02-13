package com.sitepark.ies.userrepository.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RoleSnapshotTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RoleSnapshot.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RoleSnapshot.class).verify();
  }

  @Test
  void testConstructorWithAllParameters() {

    Role role = Role.builder().name("TestRole").build();
    List<String> userIds = List.of("user1", "user2");
    List<String> privilegeIds = List.of("priv1", "priv2");

    RoleSnapshot snapshot = new RoleSnapshot(role, userIds, privilegeIds);

    assertEquals(role, snapshot.role(), "role should match");
  }

  @Test
  void testConstructorWithNullUserIds() {

    Role role = Role.builder().name("TestRole").build();

    RoleSnapshot snapshot = new RoleSnapshot(role, null, List.of("priv1"));

    assertTrue(snapshot.userIds().isEmpty(), "userIds should be empty when null is provided");
  }

  @Test
  void testConstructorWithNullPrivilegeIds() {

    Role role = Role.builder().name("TestRole").build();

    RoleSnapshot snapshot = new RoleSnapshot(role, List.of("user1"), null);

    assertTrue(
        snapshot.privilegesIds().isEmpty(), "privilegeIds should be empty when null is provided");
  }

  @Test
  void testUserIdsReturnsDefensiveCopy() {

    Role role = Role.builder().name("TestRole").build();
    List<String> userIds = List.of("user1", "user2");

    RoleSnapshot snapshot = new RoleSnapshot(role, userIds, List.of());

    assertEquals(2, snapshot.userIds().size(), "userIds should contain all IDs");
  }

  @Test
  void testPrivilegeIdsReturnsDefensiveCopy() {

    Role role = Role.builder().name("TestRole").build();
    List<String> privilegeIds = List.of("priv1", "priv2");

    RoleSnapshot snapshot = new RoleSnapshot(role, List.of(), privilegeIds);

    assertEquals(2, snapshot.privilegesIds().size(), "privilegeIds should contain all IDs");
  }
}
