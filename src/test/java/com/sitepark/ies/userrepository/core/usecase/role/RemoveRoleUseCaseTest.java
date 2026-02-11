package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.service.RoleEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveRoleUseCaseTest {

  private RoleRepository repository;
  private RoleEntityAuthorizationService roleEntityAuthorizationService;
  private final Clock fixedClock =
      Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

  private RemoveRoleUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    RoleAssigner roleAssigner = mock();
    this.roleEntityAuthorizationService = mock();

    this.useCase =
        new RemoveRoleUseCase(
            this.repository, roleAssigner, this.roleEntityAuthorizationService, this.fixedClock);
  }

  @Test
  void testAccessDenied() {
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.removeRole(RemoveRoleRequest.builder().id("2").build()),
        "Expected AccessDeniedException when not allowed to remove role");
  }

  @Test
  void testRemoveWithId() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRole(RemoveRoleRequest.builder().id("2").build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveWithAnchorNotFound() {
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () -> this.useCase.removeRole(RemoveRoleRequest.builder().anchor("myanchor").build()),
        "Expected AnchorNotFoundException for non-existing anchor");
  }

  @Test
  void testRemoveWithAnchor() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRole(RemoveRoleRequest.builder().anchor("myanchor").build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveReturnsRemovedResultType() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    RemoveRoleResult result = this.useCase.removeRole(RemoveRoleRequest.builder().id("2").build());

    assertTrue(result instanceof RemoveRoleResult.Removed, "Expected Removed result type");
  }

  @Test
  void testRemoveReturnsCorrectRoleId() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    RemoveRoleResult result = this.useCase.removeRole(RemoveRoleRequest.builder().id("2").build());

    RemoveRoleResult.Removed removed = (RemoveRoleResult.Removed) result;
    assertEquals("2", removed.roleId(), "Expected role ID 2");
  }

  @Test
  void testRemoveBuiltInAdministratorRoleSkipped() {
    RemoveRoleResult result = this.useCase.removeRole(RemoveRoleRequest.builder().id("1").build());

    assertTrue(result instanceof RemoveRoleResult.Skipped, "Expected Skipped result type");
  }

  @Test
  void testRemoveCallsRepositoryRemove() {
    Role role = Role.builder().id("2").name("test").build();
    when(this.roleEntityAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(role));

    this.useCase.removeRole(RemoveRoleRequest.builder().id("2").build());

    verify(this.repository).remove("2");
  }
}
