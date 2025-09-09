package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateRoleTest {

  private RoleRepository repository;
  private RoleAssigner roleAssigner;
  private AccessControl accessControl;

  private UpdateRole useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(RoleRepository.class);
    this.roleAssigner = mock(RoleAssigner.class);
    this.accessControl = mock(AccessControl.class);

    this.useCase = new UpdateRole(this.repository, this.roleAssigner, this.accessControl);
  }

  @Test
  void testAccessDenied() {

    when(this.accessControl.isRoleWritable()).thenReturn(false);

    Role role = Role.builder().id("1").name("test").build();

    assertThrows(AccessDeniedException.class, () -> this.useCase.updateRole(role, null));
  }

  @Test
  void testNoIdNoAnchor() {
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    Role role = Role.builder().name("test").build();

    assertThrows(IllegalArgumentException.class, () -> this.useCase.updateRole(role, null));
  }

  @Test
  void testAnchorNotFound() {
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    Role role = Role.builder().anchor("test").name("test").build();

    assertThrows(AnchorNotFoundException.class, () -> this.useCase.updateRole(role, null));
  }

  @Test
  void testAnchorAlreadyExists() {
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    Role role = Role.builder().id("1").anchor("test").name("test").build();

    assertThrows(AnchorAlreadyExistsException.class, () -> this.useCase.updateRole(role, null));
  }

  @Test
  void testUpdateWithAnchor() {
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));

    Role role = Role.builder().anchor("test").name("test").build();
    this.useCase.updateRole(role, null);

    Role expected = role.toBuilder().id("1").build();
    verify(this.repository).update(expected);
  }

  @Test
  void testUpdateReturnId() {
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));

    Role role = Role.builder().anchor("test").name("test").build();
    String id = this.useCase.updateRole(role, null);
    assertEquals("1", id, "id should be resolved from anchor");
  }

  @Test
  void testUpdateWithPrivilegeIds() {
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));

    Role role = Role.builder().id("1").name("test").build();
    this.useCase.updateRole(role, new String[] {"12"});

    verify(this.roleAssigner).reassignPrivilegesToRoles(List.of("1"), List.of("12"));
  }
}
