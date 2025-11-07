package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.sharedkernel.security.PermissionPayload;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertPrivilegeUseCaseTest {

  private AccessControl accessControl;
  private PrivilegeRepository repository;
  private CreatePrivilegeUseCase createPrivilegeUseCase;
  private UpdatePrivilegeUseCase updatePrivilegeUseCase;

  private UpsertPrivilegeUseCase useCase;

  @BeforeEach
  void setUp() {
    this.accessControl = mock(AccessControl.class);
    this.repository = mock(PrivilegeRepository.class);
    this.createPrivilegeUseCase = mock(CreatePrivilegeUseCase.class);
    this.updatePrivilegeUseCase = mock(UpdatePrivilegeUseCase.class);

    this.useCase =
        new UpsertPrivilegeUseCase(
            this.accessControl,
            this.repository,
            this.createPrivilegeUseCase,
            this.updatePrivilegeUseCase);
  }

  @Test
  void testAccessDeniedCreatable() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(false);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("test")
            .permission(new PermissionPayload("test", null))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.upsertPrivilege(
                UpsertPrivilegeRequest.builder().privilege(privilege).build()),
        "upsert privilege should be denied");
  }

  @Test
  void testAccessDeniedWritable() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(false);

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("test")
            .permission(new PermissionPayload("test", null))
            .build();
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.upsertPrivilege(
                UpsertPrivilegeRequest.builder().privilege(privilege).build()),
        "upsert privilege should be denied");
  }

  @Test
  void testWithoutIdAndAnchor() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);

    Privilege privilege =
        Privilege.builder().name("test").permission(new PermissionPayload("test", null)).build();

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    verify(this.createPrivilegeUseCase)
        .createPrivilege(CreatePrivilegeRequest.builder().privilege(privilege).build());
  }

  @Test
  void testWithId() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .name("test")
            .permission(new PermissionPayload("test", null))
            .build();

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    verify(this.updatePrivilegeUseCase)
        .updatePrivilege(UpdatePrivilegeRequest.builder().privilege(privilege).build());
  }

  @Test
  void testWithUnknownAnchor() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);

    Privilege privilege =
        Privilege.builder()
            .anchor("anchor")
            .name("test")
            .permission(new PermissionPayload("test", null))
            .build();

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    verify(this.createPrivilegeUseCase)
        .createPrivilege(CreatePrivilegeRequest.builder().privilege(privilege).build());
  }

  @Test
  void testWithKnownAnchor() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));

    Privilege privilege =
        Privilege.builder()
            .anchor("anchor")
            .name("test")
            .permission(new PermissionPayload("test", null))
            .build();

    this.useCase.upsertPrivilege(UpsertPrivilegeRequest.builder().privilege(privilege).build());

    Privilege expected = privilege.toBuilder().id("1").build();

    verify(this.updatePrivilegeUseCase)
        .updatePrivilege(UpdatePrivilegeRequest.builder().privilege(expected).build());
  }

  @Test
  void testWithAlreadyExistsAnchor() {

    when(this.accessControl.isPrivilegeCreatable()).thenReturn(true);
    when(this.accessControl.isPrivilegeWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    Privilege privilege =
        Privilege.builder()
            .id("1")
            .anchor("anchor")
            .name("test")
            .permission(new PermissionPayload("test", null))
            .build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () ->
            this.useCase.upsertPrivilege(
                UpsertPrivilegeRequest.builder().privilege(privilege).build()),
        "Expected AnchorAlreadyExistsException for privilege with existing anchor");
  }
}
