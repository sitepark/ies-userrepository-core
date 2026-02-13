package com.sitepark.ies.userrepository.core.domain.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.ToStringVerifier;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PrivilegeRoleAssignmentTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(PrivilegeRoleAssignment.class)
        .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(PrivilegeRoleAssignment.class).verify();
  }

  @Test
  void testBuildEmpty() {

    PrivilegeRoleAssignment assignment = PrivilegeRoleAssignment.builder().build();

    assertTrue(assignment.isEmpty(), "empty assignment should be empty");
  }

  @Test
  void testIsEmptyReturnsFalse() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();

    assertFalse(assignment.isEmpty(), "assignment with data should not be empty");
  }

  @Test
  void testSizeReturnsNumberOfPrivileges() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder()
            .assignment("priv1", "role1")
            .assignment("priv2", "role2")
            .build();

    assertEquals(2, assignment.size(), "size should return number of privileges");
  }

  @Test
  void testPrivilegeIdsReturnsAllPrivilegeIds() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder()
            .assignment("priv1", "role1")
            .assignment("priv2", "role2")
            .build();

    List<String> privilegeIds = assignment.privilegeIds();

    assertEquals(2, privilegeIds.size(), "should return all privilege IDs");
  }

  @Test
  void testRoleIdsForSpecificPrivilege() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder()
            .assignment("priv1", "role1")
            .assignment("priv1", "role2")
            .build();

    List<String> roleIds = assignment.roleIds("priv1");

    assertEquals(2, roleIds.size(), "should return all role IDs for privilege");
  }

  @Test
  void testRoleIdsForNonExistentPrivilege() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();

    List<String> roleIds = assignment.roleIds("nonexistent");

    assertTrue(roleIds.isEmpty(), "should return empty list for non-existent privilege");
  }

  @Test
  void testRoleIdsReturnsAllRoleIds() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder()
            .assignment("priv1", "role1")
            .assignment("priv2", "role2")
            .assignment("priv1", "role3")
            .build();

    List<String> roleIds = assignment.roleIds();

    assertEquals(3, roleIds.size(), "should return all unique role IDs");
  }

  @Test
  void testBuilderAssignmentWithList() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder().assignments("priv1", List.of("role1", "role2")).build();

    List<String> roleIds = assignment.roleIds("priv1");

    assertEquals(2, roleIds.size(), "should add all roles from list");
  }

  @Test
  void testBuilderAssignmentWithVarargs() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder().assignments("priv1", "role1", "role2").build();

    List<String> roleIds = assignment.roleIds("priv1");

    assertEquals(2, roleIds.size(), "should add all roles from varargs");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdsListThrows() {

    PrivilegeRoleAssignment.Builder builder = PrivilegeRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("priv1", (List<String>) null),
        "should throw NPE for null roleIds list");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdsVarargsThrows() {

    PrivilegeRoleAssignment.Builder builder = PrivilegeRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("priv1", (String[]) null),
        "should throw NPE for null roleIds varargs");
  }

  @Test
  void testBuilderAssignmentWithNullPrivilegeIdThrows() {

    PrivilegeRoleAssignment.Builder builder = PrivilegeRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment(null, "role1"),
        "should throw NPE for null privilegeId");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdThrows() {

    PrivilegeRoleAssignment.Builder builder = PrivilegeRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment("priv1", null),
        "should throw NPE for null roleId");
  }

  @Test
  void testToBuilder() {

    PrivilegeRoleAssignment original =
        PrivilegeRoleAssignment.builder()
            .assignment("priv1", "role1")
            .assignment("priv2", "role2")
            .build();

    PrivilegeRoleAssignment copy = original.toBuilder().build();

    assertEquals(original, copy, "toBuilder should create equal copy");
  }

  @Test
  void testToBuilderAllowsModification() {

    PrivilegeRoleAssignment original =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();

    PrivilegeRoleAssignment modified = original.toBuilder().assignment("priv2", "role2").build();

    assertNotEquals(original, modified, "modified copy should not equal original");
  }

  @Test
  void testEqualsWithSameData() {

    PrivilegeRoleAssignment assignment1 =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();
    PrivilegeRoleAssignment assignment2 =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();

    assertEquals(assignment1, assignment2, "assignments with same data should be equal");
  }

  @Test
  void testEqualsWithDifferentData() {

    PrivilegeRoleAssignment assignment1 =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();
    PrivilegeRoleAssignment assignment2 =
        PrivilegeRoleAssignment.builder().assignment("priv2", "role2").build();

    assertNotEquals(
        assignment1, assignment2, "assignments with different data should not be equal");
  }

  @Test
  void testHashCodeConsistency() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();

    int hashCode1 = assignment.hashCode();
    int hashCode2 = assignment.hashCode();

    assertEquals(hashCode1, hashCode2, "hashCode should be consistent");
  }

  @Test
  void testToStringContainsAssignments() {

    PrivilegeRoleAssignment assignment =
        PrivilegeRoleAssignment.builder().assignment("priv1", "role1").build();

    String toString = assignment.toString();

    assertTrue(toString.contains("PrivilegeRoleAssignment"), "toString should contain class name");
  }
}
