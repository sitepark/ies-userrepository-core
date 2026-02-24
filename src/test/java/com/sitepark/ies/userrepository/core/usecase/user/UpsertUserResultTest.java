package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jparams.verifier.tostring.ToStringVerifier;
import java.time.Instant;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UpsertUserResultTest {

  @Test
  void testEqualsCreated() {
    EqualsVerifier.forClass(UpsertUserResult.Created.class).verify();
  }

  @Test
  void testToStringCreated() {
    ToStringVerifier.forClass(UpsertUserResult.Created.class).verify();
  }

  @Test
  void testEqualsUpdated() {
    EqualsVerifier.forClass(UpsertUserResult.Updated.class).verify();
  }

  @Test
  void testToStringUpdated() {
    ToStringVerifier.forClass(UpsertUserResult.Updated.class).verify();
  }

  @Test
  void testCreatedFactoryMethod() {
    CreateUserResult createResult = new CreateUserResult("123", null, null, null);
    UpsertUserResult result = UpsertUserResult.created("123", createResult);
    assertEquals(
        "123",
        result.userId(),
        "created() factory method should return result with correct userId");
  }

  @Test
  void testUpdatedFactoryMethod() {
    UpdateUserResult updateResult =
        new UpdateUserResult(
            "456",
            Instant.now(),
            UserUpdateResult.unchanged(),
            ReassignRolesToUsersResult.skipped());
    UpsertUserResult result = UpsertUserResult.updated("456", updateResult);
    assertEquals(
        "456",
        result.userId(),
        "updated() factory method should return result with correct userId");
  }
}
