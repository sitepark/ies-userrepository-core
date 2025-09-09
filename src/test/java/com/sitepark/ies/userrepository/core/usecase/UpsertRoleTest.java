package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpsertRoleTest {

  private AccessControl accessControl;
  private RoleRepository repository;
  private CreateRole createRoleUseCase;
  private UpdateRole updateRoleUseCase;

  private UpsertRole useCase;

  @BeforeEach
  void setUp() {
    this.accessControl = mock(AccessControl.class);
    this.repository = mock(RoleRepository.class);
    this.createRoleUseCase = mock(CreateRole.class);
    this.updateRoleUseCase = mock(UpdateRole.class);

    this.useCase =
        new UpsertRole(
            this.accessControl, this.repository, this.createRoleUseCase, this.updateRoleUseCase);
  }

  @Test
  void testAccessDeniedCreatable() {

    when(this.accessControl.isRoleCreatable()).thenReturn(false);
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    Role role = Role.builder().name("test").build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.upsertRole(role, null),
        "upsert role should be denied");
  }

  @Test
  void testAccessDeniedWritable() {

    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(false);

    Role role = Role.builder().name("test").build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.upsertRole(role, null),
        "upsert role should be denied");
  }

  @Test
  void testWithoutIdAndAnchor() {

    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    Role role = Role.builder().name("test").build();

    this.useCase.upsertRole(role, null);

    verify(this.createRoleUseCase).createRole(role, null);
  }

  @Test
  void testWithId() {

    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    Role role = Role.builder().id("1").name("test").build();

    this.useCase.upsertRole(role, null);

    verify(this.updateRoleUseCase).updateRole(role, null);
  }

  @Test
  void testWithUnknownAnchor() {

    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);

    Role role = Role.builder().anchor("anchor").name("test").build();

    this.useCase.upsertRole(role, null);

    verify(this.createRoleUseCase).createRole(role, null);
  }

  @Test
  void testWithKnownAnchor() {

    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("1"));

    Role role = Role.builder().anchor("anchor").name("test").build();

    this.useCase.upsertRole(role, null);

    Role expected = role.toBuilder().id("1").build();

    verify(this.updateRoleUseCase).updateRole(expected, null);
  }

  @Test
  void testWithAlreadyExistsAnchor() {

    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(java.util.Optional.of("2"));

    Role role = Role.builder().id("1").anchor("anchor").name("test").build();

    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.useCase.upsertRole(role, null),
        "Expected AnchorAlreadyExistsException for role with existing anchor");
  }
}
