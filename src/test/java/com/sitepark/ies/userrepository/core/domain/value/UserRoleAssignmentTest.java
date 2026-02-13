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

class UserRoleAssignmentTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(UserRoleAssignment.class)
        .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE)
        .verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(UserRoleAssignment.class).verify();
  }

  @Test
  void testBuildEmpty() {

    UserRoleAssignment assignment = UserRoleAssignment.builder().build();

    assertTrue(assignment.isEmpty(), "empty assignment should be empty");
  }

  @Test
  void testIsEmptyReturnsFalse() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder().assignment("user1", "role1").build();

    assertFalse(assignment.isEmpty(), "assignment with data should not be empty");
  }

  @Test
  void testSizeReturnsNumberOfAssignments() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder()
            .assignment("user1", "role1")
            .assignment("user1", "role2")
            .assignment("user2", "role3")
            .build();

    assertEquals(3, assignment.size(), "size should return total number of assignments");
  }

  @Test
  void testUserIdsReturnsAllUserIds() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder()
            .assignment("user1", "role1")
            .assignment("user2", "role2")
            .build();

    List<String> userIds = assignment.userIds();

    assertEquals(2, userIds.size(), "should return all user IDs");
  }

  @Test
  void testRoleIdsForSpecificUser() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder()
            .assignment("user1", "role1")
            .assignment("user1", "role2")
            .build();

    List<String> roleIds = assignment.roleIds("user1");

    assertEquals(2, roleIds.size(), "should return all role IDs for user");
  }

  @Test
  void testRoleIdsForNonExistentUser() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder().assignment("user1", "role1").build();

    List<String> roleIds = assignment.roleIds("nonexistent");

    assertTrue(roleIds.isEmpty(), "should return empty list for non-existent user");
  }

  @Test
  void testRoleIdsReturnsAllRoleIds() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder()
            .assignment("user1", "role1")
            .assignment("user2", "role2")
            .assignment("user1", "role3")
            .build();

    List<String> roleIds = assignment.roleIds();

    assertEquals(3, roleIds.size(), "should return all unique role IDs");
  }

  @Test
  void testBuilderAssignmentWithList() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder().assignments("user1", List.of("role1", "role2")).build();

    List<String> roleIds = assignment.roleIds("user1");

    assertEquals(2, roleIds.size(), "should add all roles from list");
  }

  @Test
  void testBuilderAssignmentWithVarargs() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder().assignments("user1", "role1", "role2").build();

    List<String> roleIds = assignment.roleIds("user1");

    assertEquals(2, roleIds.size(), "should add all roles from varargs");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdsListThrows() {

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("user1", (List<String>) null),
        "should throw NPE for null roleIds list");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdsVarargsThrows() {

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignments("user1", (String[]) null),
        "should throw NPE for null roleIds varargs");
  }

  @Test
  void testBuilderAssignmentWithNullUserIdThrows() {

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment(null, "role1"),
        "should throw NPE for null userId");
  }

  @Test
  void testBuilderAssignmentWithNullRoleIdThrows() {

    UserRoleAssignment.Builder builder = UserRoleAssignment.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.assignment("user1", null),
        "should throw NPE for null roleId");
  }

  @Test
  void testToBuilder() {

    UserRoleAssignment original =
        UserRoleAssignment.builder()
            .assignment("user1", "role1")
            .assignment("user2", "role2")
            .build();

    UserRoleAssignment copy = original.toBuilder().build();

    assertEquals(original, copy, "toBuilder should create equal copy");
  }

  @Test
  void testToBuilderAllowsModification() {

    UserRoleAssignment original = UserRoleAssignment.builder().assignment("user1", "role1").build();

    UserRoleAssignment modified = original.toBuilder().assignment("user2", "role2").build();

    assertNotEquals(original, modified, "modified copy should not equal original");
  }

  @Test
  void testEqualsWithSameData() {

    UserRoleAssignment assignment1 =
        UserRoleAssignment.builder().assignment("user1", "role1").build();
    UserRoleAssignment assignment2 =
        UserRoleAssignment.builder().assignment("user1", "role1").build();

    assertEquals(assignment1, assignment2, "assignments with same data should be equal");
  }

  @Test
  void testEqualsWithDifferentData() {

    UserRoleAssignment assignment1 =
        UserRoleAssignment.builder().assignment("user1", "role1").build();
    UserRoleAssignment assignment2 =
        UserRoleAssignment.builder().assignment("user2", "role2").build();

    assertNotEquals(
        assignment1, assignment2, "assignments with different data should not be equal");
  }

  @Test
  void testHashCodeConsistency() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder().assignment("user1", "role1").build();

    int hashCode1 = assignment.hashCode();
    int hashCode2 = assignment.hashCode();

    assertEquals(hashCode1, hashCode2, "hashCode should be consistent");
  }

  @Test
  void testToStringContainsAssignments() {

    UserRoleAssignment assignment =
        UserRoleAssignment.builder().assignment("user1", "role1").build();

    String toString = assignment.toString();

    assertTrue(toString.contains("PrivilegeRoleAssignment"), "toString should contain class name");
  }
}
