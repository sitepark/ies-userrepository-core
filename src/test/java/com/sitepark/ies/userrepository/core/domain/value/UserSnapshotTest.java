package com.sitepark.ies.userrepository.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UserSnapshotTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UserSnapshot.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UserSnapshot.class).verify();
  }

  @Test
  void testConstructorWithAllParameters() {

    User user = User.builder().login("testuser").build();
    List<String> roleIds = List.of("role1", "role2");

    UserSnapshot snapshot = new UserSnapshot(user, roleIds);

    assertEquals(user, snapshot.user(), "user should match");
  }

  @Test
  void testConstructorWithNullRoleIds() {

    User user = User.builder().login("testuser").build();

    UserSnapshot snapshot = new UserSnapshot(user, null);

    assertTrue(snapshot.roleIds().isEmpty(), "roleIds should be empty when null is provided");
  }

  @Test
  void testRoleIdsReturnsDefensiveCopy() {

    User user = User.builder().login("testuser").build();
    List<String> roleIds = List.of("role1", "role2");

    UserSnapshot snapshot = new UserSnapshot(user, roleIds);

    assertEquals(2, snapshot.roleIds().size(), "roleIds should contain all IDs");
  }
}
