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

class RoleUserAssignmentTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RoleUserAssignment.class)
        .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RoleUserAssignment.class).verify();
  }

  @Test
  void testBuildEmpty() {

    RoleUserAssignment assignment = RoleUserAssignment.builder().build();

    assertTrue(assignment.isEmpty(), "empty assignment should be empty");
  }

  @Test
  void testIsEmptyReturnsFalse() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder().assignment("role1", "user1").build();

    assertFalse(assignment.isEmpty(), "assignment with data should not be empty");
  }

  @Test
  void testSizeReturnsNumberOfRoles() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder()
            .assignment("role1", "user1")
            .assignment("role2", "user2")
            .build();

    assertEquals(2, assignment.size(), "size should return number of roles");
  }

  @Test
  void testRoleIdsReturnsAllRoleIds() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder()
            .assignment("role1", "user1")
            .assignment("role2", "user2")
            .build();

    List<String> roleIds = assignment.roleIds();

    assertEquals(2, roleIds.size(), "should return all role IDs");
  }

  @Test
  void testUserIdsForSpecificRole() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder()
            .assignment("role1", "user1")
            .assignment("role1", "user2")
            .build();

    List<String> userIds = assignment.userIds("role1");

    assertEquals(2, userIds.size(), "should return all user IDs for role");
  }

  @Test
  void testUserIdsForNonExistentRole() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder().assignment("role1", "user1").build();

    List<String> userIds = assignment.userIds("nonexistent");

    assertTrue(userIds.isEmpty(), "should return empty list for non-existent role");
  }

  @Test
  void testUserIdsReturnsAllUserIds() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder()
            .assignment("role1", "user1")
            .assignment("role2", "user2")
            .assignment("role1", "user3")
            .build();

    List<String> userIds = assignment.userIds();

    assertEquals(3, userIds.size(), "should return all unique user IDs");
  }

  @Test
  void testBuilderAssignmentWithList() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder().assignments("role1", List.of("user1", "user2")).build();

    List<String> userIds = assignment.userIds("role1");

    assertEquals(2, userIds.size(), "should add all users from list");
  }

  @Test
  void testBuilderAssignmentWithVarargs() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder().assignments("role1", "user1", "user2").build();

    List<String> userIds = assignment.userIds("role1");

    assertEquals(2, userIds.size(), "should add all users from varargs");
  }

  @Test
  void testBuilderAssignmentWithNullUserIdsListThrows() {

    RoleUserAssignment.Builder builder = RoleUserAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("role1", (List<String>) null),
        "should throw NPE for null userIds list");
  }

  @Test
  void testBuilderAssignmentWithNullUserIdsVarargsThrows() {

    RoleUserAssignment.Builder builder = RoleUserAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("role1", (String[]) null),
        "should throw NPE for null userIds varargs");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdThrows() {

    RoleUserAssignment.Builder builder = RoleUserAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment(null, "user1"),
        "should throw NPE for null roleId");
  }

  @Test
  void testBuilderAssignmentWithNullUserIdThrows() {

    RoleUserAssignment.Builder builder = RoleUserAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment("role1", null),
        "should throw NPE for null userId");
  }

  @Test
  void testToBuilder() {

    RoleUserAssignment original =
        RoleUserAssignment.builder()
            .assignment("role1", "user1")
            .assignment("role2", "user2")
            .build();

    RoleUserAssignment copy = original.toBuilder().build();

    assertEquals(original, copy, "toBuilder should create equal copy");
  }

  @Test
  void testToBuilderAllowsModification() {

    RoleUserAssignment original = RoleUserAssignment.builder().assignment("role1", "user1").build();

    RoleUserAssignment modified = original.toBuilder().assignment("role2", "user2").build();

    assertNotEquals(original, modified, "modified copy should not equal original");
  }

  @Test
  void testEqualsWithSameData() {

    RoleUserAssignment assignment1 =
        RoleUserAssignment.builder().assignment("role1", "user1").build();
    RoleUserAssignment assignment2 =
        RoleUserAssignment.builder().assignment("role1", "user1").build();

    assertEquals(assignment1, assignment2, "assignments with same data should be equal");
  }

  @Test
  void testEqualsWithDifferentData() {

    RoleUserAssignment assignment1 =
        RoleUserAssignment.builder().assignment("role1", "user1").build();
    RoleUserAssignment assignment2 =
        RoleUserAssignment.builder().assignment("role2", "user2").build();

    assertNotEquals(
        assignment1, assignment2, "assignments with different data should not be equal");
  }

  @Test
  void testHashCodeConsistency() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder().assignment("role1", "user1").build();

    int hashCode1 = assignment.hashCode();
    int hashCode2 = assignment.hashCode();

    assertEquals(hashCode1, hashCode2, "hashCode should be consistent");
  }

  @Test
  void testToStringContainsAssignments() {

    RoleUserAssignment assignment =
        RoleUserAssignment.builder().assignment("role1", "user1").build();

    String toString = assignment.toString();

    assertTrue(toString.contains("PrivilegeRoleAssignment"), "toString should contain class name");
  }
}
