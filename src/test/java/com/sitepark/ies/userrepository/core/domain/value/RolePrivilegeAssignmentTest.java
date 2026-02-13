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

class RolePrivilegeAssignmentTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RolePrivilegeAssignment.class)
        .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RolePrivilegeAssignment.class).verify();
  }

  @Test
  void testBuildEmpty() {

    RolePrivilegeAssignment assignment = RolePrivilegeAssignment.builder().build();

    assertTrue(assignment.isEmpty(), "empty assignment should be empty");
  }

  @Test
  void testIsEmptyReturnsFalse() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();

    assertFalse(assignment.isEmpty(), "assignment with data should not be empty");
  }

  @Test
  void testSizeReturnsNumberOfAssignments() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder()
            .assignment("role1", "priv1")
            .assignment("role1", "priv2")
            .assignment("role2", "priv3")
            .build();

    assertEquals(3, assignment.size(), "size should return total number of assignments");
  }

  @Test
  void testRoleIdsReturnsAllRoleIds() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder()
            .assignment("role1", "priv1")
            .assignment("role2", "priv2")
            .build();

    List<String> roleIds = assignment.roleIds();

    assertEquals(2, roleIds.size(), "should return all role IDs");
  }

  @Test
  void testPrivilegeIdsForSpecificRole() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder()
            .assignment("role1", "priv1")
            .assignment("role1", "priv2")
            .build();

    List<String> privilegeIds = assignment.privilegeIds("role1");

    assertEquals(2, privilegeIds.size(), "should return all privilege IDs for role");
  }

  @Test
  void testPrivilegeIdsForNonExistentRole() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();

    List<String> privilegeIds = assignment.privilegeIds("nonexistent");

    assertTrue(privilegeIds.isEmpty(), "should return empty list for non-existent role");
  }

  @Test
  void testPrivilegeIdsReturnsAllPrivilegeIds() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder()
            .assignment("role1", "priv1")
            .assignment("role2", "priv2")
            .assignment("role1", "priv3")
            .build();

    List<String> privilegeIds = assignment.privilegeIds();

    assertEquals(3, privilegeIds.size(), "should return all unique privilege IDs");
  }

  @Test
  void testBuilderAssignmentWithList() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder().assignments("role1", List.of("priv1", "priv2")).build();

    List<String> privilegeIds = assignment.privilegeIds("role1");

    assertEquals(2, privilegeIds.size(), "should add all privileges from list");
  }

  @Test
  void testBuilderAssignmentWithVarargs() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder().assignments("role1", "priv1", "priv2").build();

    List<String> privilegeIds = assignment.privilegeIds("role1");

    assertEquals(2, privilegeIds.size(), "should add all privileges from varargs");
  }

  @Test
  void testBuilderAssignmentWithNullPrivilegeIdsListThrows() {

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("role1", (List<String>) null),
        "should throw NPE for null privilegeIds list");
  }

  @Test
  void testBuilderAssignmentWithNullPrivilegeIdsVarargsThrows() {

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("role1", (String[]) null),
        "should throw NPE for null privilegeIds varargs");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdThrows() {

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment(null, "priv1"),
        "should throw NPE for null roleId");
  }

  @Test
  void testBuilderAssignmentWithNullPrivilegeIdThrows() {

    RolePrivilegeAssignment.Builder builder = RolePrivilegeAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment("role1", null),
        "should throw NPE for null privilegeId");
  }

  @Test
  void testToBuilder() {

    RolePrivilegeAssignment original =
        RolePrivilegeAssignment.builder()
            .assignment("role1", "priv1")
            .assignment("role2", "priv2")
            .build();

    RolePrivilegeAssignment copy = original.toBuilder().build();

    assertEquals(original, copy, "toBuilder should create equal copy");
  }

  @Test
  void testToBuilderAllowsModification() {

    RolePrivilegeAssignment original =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();

    RolePrivilegeAssignment modified = original.toBuilder().assignment("role2", "priv2").build();

    assertNotEquals(original, modified, "modified copy should not equal original");
  }

  @Test
  void testEqualsWithSameData() {

    RolePrivilegeAssignment assignment1 =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();
    RolePrivilegeAssignment assignment2 =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();

    assertEquals(assignment1, assignment2, "assignments with same data should be equal");
  }

  @Test
  void testEqualsWithDifferentData() {

    RolePrivilegeAssignment assignment1 =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();
    RolePrivilegeAssignment assignment2 =
        RolePrivilegeAssignment.builder().assignment("role2", "priv2").build();

    assertNotEquals(
        assignment1, assignment2, "assignments with different data should not be equal");
  }

  @Test
  void testHashCodeConsistency() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();

    int hashCode1 = assignment.hashCode();
    int hashCode2 = assignment.hashCode();

    assertEquals(hashCode1, hashCode2, "hashCode should be consistent");
  }

  @Test
  void testToStringContainsAssignments() {

    RolePrivilegeAssignment assignment =
        RolePrivilegeAssignment.builder().assignment("role1", "priv1").build();

    String toString = assignment.toString();

    assertTrue(toString.contains("PrivilegeRoleAssignment"), "toString should contain class name");
  }
}
