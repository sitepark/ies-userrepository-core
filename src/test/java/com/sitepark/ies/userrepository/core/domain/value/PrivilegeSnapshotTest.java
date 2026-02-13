package com.sitepark.ies.userrepository.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PrivilegeSnapshotTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(PrivilegeSnapshot.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(PrivilegeSnapshot.class).verify();
  }

  @Test
  void testConstructorWithAllParameters() {

    Privilege privilege = Privilege.builder().name("TestPrivilege").build();
    List<String> roleIds = List.of("role1", "role2");

    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, roleIds);

    assertEquals(privilege, snapshot.privilege(), "privilege should match");
  }

  @Test
  void testConstructorWithNullRoleIds() {

    Privilege privilege = Privilege.builder().name("TestPrivilege").build();

    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, null);

    assertTrue(snapshot.roleIds().isEmpty(), "roleIds should be empty when null is provided");
  }

  @Test
  void testRoleIdsReturnsDefensiveCopy() {

    Privilege privilege = Privilege.builder().name("TestPrivilege").build();
    List<String> roleIds = List.of("role1", "role2");

    PrivilegeSnapshot snapshot = new PrivilegeSnapshot(privilege, roleIds);

    assertEquals(2, snapshot.roleIds().size(), "roleIds should contain all IDs");
  }
}
