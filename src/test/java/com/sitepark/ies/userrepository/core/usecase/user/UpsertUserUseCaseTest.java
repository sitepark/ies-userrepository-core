package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertUserUseCaseTest {

  private UserRepository repository;
  private CreateUserUseCase createUserUseCase;
  private UpdateUserUseCase updateUserUseCase;

  private UpsertUserUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(UserRepository.class);
    this.createUserUseCase = mock(CreateUserUseCase.class);
    this.updateUserUseCase = mock(UpdateUserUseCase.class);

    this.useCase =
        new UpsertUserUseCase(this.repository, this.createUserUseCase, this.updateUserUseCase);
  }

  @Test
  void testWithoutIdAndAnchor() {

    when(this.createUserUseCase.createUser(any()))
        .thenReturn(new CreateUserResult("123", null, null, null));

    User user = User.builder().login("test").lastName("test").build();

    this.useCase.upsertUser(UpsertUserRequest.builder().user(user).build());

    verify(this.createUserUseCase).createUser(CreateUserRequest.builder().user(user).build());
  }

  @Test
  void testWithId() {

    when(this.updateUserUseCase.updateUser(any()))
        .thenReturn(
            new UpdateUserResult(
                "1",
                Instant.now(),
                UserUpdateResult.unchanged(),
                ReassignRolesToUsersResult.skipped()));

    User user = User.builder().id("1").login("test").lastName("test").build();

    this.useCase.upsertUser(UpsertUserRequest.builder().user(user).build());

    verify(this.updateUserUseCase).updateUser(UpdateUserRequest.builder().user(user).build());
  }

  @Test
  void testWithUnknownAnchor() {

    when(this.createUserUseCase.createUser(any()))
        .thenReturn(new CreateUserResult("123", null, null, null));

    User user = User.builder().anchor("anchor").login("test").lastName("test").build();

    this.useCase.upsertUser(UpsertUserRequest.builder().user(user).build());

    verify(this.createUserUseCase).createUser(CreateUserRequest.builder().user(user).build());
  }

  @Test
  void testWithKnownAnchor() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));
    when(this.updateUserUseCase.updateUser(any()))
        .thenReturn(
            new UpdateUserResult(
                "1",
                Instant.now(),
                UserUpdateResult.unchanged(),
                ReassignRolesToUsersResult.skipped()));

    User user = User.builder().anchor("anchor").login("test").lastName("test").build();

    this.useCase.upsertUser(UpsertUserRequest.builder().user(user).build());

    User expected = user.toBuilder().id("1").build();

    verify(this.updateUserUseCase).updateUser(UpdateUserRequest.builder().user(expected).build());
  }

  @Test
  void testWithAlreadyExistsAnchor() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    User user = User.builder().id("1").anchor("anchor").login("test").lastName("test").build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.upsertUser(UpsertUserRequest.builder().user(user).build()),
        "Expected AnchorAlreadyExistsException for privilege with existing anchor");
  }
}
