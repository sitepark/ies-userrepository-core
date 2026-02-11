package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertPrivilegeUseCaseTest {

  private PrivilegeRepository repository;
  private CreatePrivilegeUseCase createPrivilegeUseCase;
  private UpdatePrivilegeUseCase updatePrivilegeUseCase;

  private UpsertPrivilegeUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(PrivilegeRepository.class);
    this.createPrivilegeUseCase = mock(CreatePrivilegeUseCase.class);
    this.updatePrivilegeUseCase = mock(UpdatePrivilegeUseCase.class);

    this.useCase =
        new UpsertPrivilegeUseCase(
            this.repository, this.createPrivilegeUseCase, this.updatePrivilegeUseCase);
  }

  @Test
  void testWithoutIdAndAnchorReturnsCorrectId() {

    Privilege privilege = Privilege.builder().name("test").permission(new TestPermission()).build();

    CreatePrivilegeResult mockResult = new CreatePrivilegeResult("123", null, null, Instant.now());
    when(this.createPrivilegeUseCase.createPrivilege(any())).thenReturn(mockResult);

    String result =
        this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    assertEquals("123", result, "Expected privilege ID 123");
  }

  @Test
  void testWithoutIdAndAnchorCallsCreate() {

    Privilege privilege = Privilege.builder().name("test").permission(new TestPermission()).build();

    CreatePrivilegeResult mockResult = new CreatePrivilegeResult("123", null, null, Instant.now());
    when(this.createPrivilegeUseCase.createPrivilege(any())).thenReturn(mockResult);

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    verify(this.createPrivilegeUseCase)
        .createPrivilege(CreatePrivilegeRequest.builder().privilege(privilege).build());
  }

  @Test
  void testWithIdReturnsCorrectId() {

    Privilege privilege =
        Privilege.builder().id("1").name("test").permission(new TestPermission()).build();

    UpdatePrivilegeResult mockResult =
        new UpdatePrivilegeResult("1", "test", Instant.now(), null, null, null);
    when(this.updatePrivilegeUseCase.updatePrivilege(any())).thenReturn(mockResult);

    String result =
        this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    assertEquals("1", result, "Expected privilege ID 1");
  }

  @Test
  void testWithIdCallsUpdate() {

    Privilege privilege =
        Privilege.builder().id("1").name("test").permission(new TestPermission()).build();

    UpdatePrivilegeResult mockResult =
        new UpdatePrivilegeResult("1", "test", Instant.now(), null, null, null);
    when(this.updatePrivilegeUseCase.updatePrivilege(any())).thenReturn(mockResult);

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    verify(this.updatePrivilegeUseCase)
        .updatePrivilege(UpdatePrivilegeRequest.builder().privilege(privilege).build());
  }

  @Test
  void testWithUnknownAnchorReturnsCorrectId() {

    Privilege privilege =
        Privilege.builder().anchor("anchor").name("test").permission(new TestPermission()).build();

    CreatePrivilegeResult mockResult = new CreatePrivilegeResult("456", null, null, Instant.now());
    when(this.createPrivilegeUseCase.createPrivilege(any())).thenReturn(mockResult);

    String result =
        this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    assertEquals("456", result, "Expected privilege ID 456");
  }

  @Test
  void testWithUnknownAnchorCallsCreate() {

    Privilege privilege =
        Privilege.builder().anchor("anchor").name("test").permission(new TestPermission()).build();

    CreatePrivilegeResult mockResult = new CreatePrivilegeResult("456", null, null, Instant.now());
    when(this.createPrivilegeUseCase.createPrivilege(any())).thenReturn(mockResult);

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    verify(this.createPrivilegeUseCase)
        .createPrivilege(CreatePrivilegeRequest.builder().privilege(privilege).build());
  }

  @Test
  void testWithKnownAnchorReturnsCorrectId() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));

    Privilege privilege =
        Privilege.builder().anchor("anchor").name("test").permission(new TestPermission()).build();

    UpdatePrivilegeResult mockResult =
        new UpdatePrivilegeResult("1", "test", Instant.now(), null, null, null);
    when(this.updatePrivilegeUseCase.updatePrivilege(any())).thenReturn(mockResult);

    String result =
        this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    assertEquals("1", result, "Expected privilege ID 1");
  }

  @Test
  void testWithKnownAnchorCallsUpdate() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));

    Privilege privilege =
        Privilege.builder().anchor("anchor").name("test").permission(new TestPermission()).build();

    UpdatePrivilegeResult mockResult =
        new UpdatePrivilegeResult("1", "test", Instant.now(), null, null, null);
    when(this.updatePrivilegeUseCase.updatePrivilege(any())).thenReturn(mockResult);

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    Privilege expected = privilege.toBuilder().id("1").build();

    verify(this.updatePrivilegeUseCase)
        .updatePrivilege(UpdatePrivilegeRequest.builder().privilege(expected).build());
  }

  @Test
  void testWithAlreadyExistsAnchor() {

    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("test")
            .permission(new TestPermission())
            .build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () ->
            this.useCase.upsertPrivilege(
                UpsertPrivilegeRequest.builder().privilege(privilege).build()),
        "Expected AnchorAlreadyExistsException for privilege with existing anchor");
  }
}
