package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertRoleUseCaseTest {

  private RoleRepository repository;
  private CreateRoleUseCase createRoleUseCase;
  private UpdateRoleUseCase updateRoleUseCase;

  private UpsertRoleUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(RoleRepository.class);
    this.createRoleUseCase = mock(CreateRoleUseCase.class);
    this.updateRoleUseCase = mock(UpdateRoleUseCase.class);

    this.useCase =
        new UpsertRoleUseCase(this.repository, this.createRoleUseCase, this.updateRoleUseCase);
  }

  @Test
  void testWithoutIdAndAnchorReturnsCorrectId() {

    Role role = Role.builder().name("test").build();

    CreateRoleResult mockResult = new CreateRoleResult("123", null, null, Instant.now());
    when(this.createRoleUseCase.createRole(any())).thenReturn(mockResult);

    String result = this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    assertEquals("123", result, "Expected role ID 123");
  }

  @Test
  void testWithoutIdAndAnchorCallsCreate() {

    Role role = Role.builder().name("test").build();

    CreateRoleResult mockResult = new CreateRoleResult("123", null, null, Instant.now());
    when(this.createRoleUseCase.createRole(any())).thenReturn(mockResult);

    this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    verify(this.createRoleUseCase).createRole(CreateRoleRequest.builder().role(role).build());
  }

  @Test
  void testWithIdReturnsCorrectId() {

    Role role = Role.builder().id("1").name("test").build();

    UpdateRoleResult mockResult =
        new UpdateRoleResult("1", "test", Instant.now(), null, null, null);
    when(this.updateRoleUseCase.updateRole(any())).thenReturn(mockResult);

    String result = this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    assertEquals("1", result, "Expected role ID 1");
  }

  @Test
  void testWithIdCallsUpdate() {

    Role role = Role.builder().id("1").name("test").build();

    UpdateRoleResult mockResult =
        new UpdateRoleResult("1", "test", Instant.now(), null, null, null);
    when(this.updateRoleUseCase.updateRole(any())).thenReturn(mockResult);

    this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    verify(this.updateRoleUseCase).updateRole(UpdateRoleRequest.builder().role(role).build());
  }

  @Test
  void testWithUnknownAnchorReturnsCorrectId() {

    Role role = Role.builder().anchor("anchor").name("test").build();

    CreateRoleResult mockResult = new CreateRoleResult("456", null, null, Instant.now());
    when(this.createRoleUseCase.createRole(any())).thenReturn(mockResult);

    String result = this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    assertEquals("456", result, "Expected role ID 456");
  }

  @Test
  void testWithUnknownAnchorCallsCreate() {

    Role role = Role.builder().anchor("anchor").name("test").build();

    CreateRoleResult mockResult = new CreateRoleResult("456", null, null, Instant.now());
    when(this.createRoleUseCase.createRole(any())).thenReturn(mockResult);

    this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    verify(this.createRoleUseCase).createRole(CreateRoleRequest.builder().role(role).build());
  }

  @Test
  void testWithKnownAnchorReturnsCorrectId() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));

    Role role = Role.builder().anchor("anchor").name("test").build();

    UpdateRoleResult mockResult =
        new UpdateRoleResult("1", "test", Instant.now(), null, null, null);
    when(this.updateRoleUseCase.updateRole(any())).thenReturn(mockResult);

    String result = this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    assertEquals("1", result, "Expected role ID 1");
  }

  @Test
  void testWithKnownAnchorCallsUpdate() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));

    Role role = Role.builder().anchor("anchor").name("test").build();

    UpdateRoleResult mockResult =
        new UpdateRoleResult("1", "test", Instant.now(), null, null, null);
    when(this.updateRoleUseCase.updateRole(any())).thenReturn(mockResult);

    this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build());

    Role expected = role.toBuilder().id("1").build();

    verify(this.updateRoleUseCase).updateRole(UpdateRoleRequest.builder().role(expected).build());
  }

  @Test
  void testWithAlreadyExistsAnchor() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    Role role = Role.builder().id("1").anchor("anchor").name("test").build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.upsertRole(UpsertRoleRequest.builder().role(role).build()),
        "Expected AnchorAlreadyExistsException for role with existing anchor");
  }
}
