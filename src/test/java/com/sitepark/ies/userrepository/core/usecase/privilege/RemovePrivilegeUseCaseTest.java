package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemovePrivilegeUseCaseTest {

  private PrivilegeRepository repository;
  private PrivilegeEntityAuthorizationService privilegeAuthorizationService;

  private RemovePrivilegeUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    RoleAssigner roleAssigner = mock();
    this.privilegeAuthorizationService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());
    this.useCase =
        new RemovePrivilegeUseCase(
            repository, roleAssigner, privilegeAuthorizationService, fixedClock);
  }

  @Test
  void testAccessDenied() {
    when(this.privilegeAuthorizationService.isRemovable(any(String.class))).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.useCase.removePrivilege(RemovePrivilegeRequest.builder().id("2").build()),
        "Expected AccessDeniedException for removing privilege without permission");
  }

  @Test
  void testAnchorNotFound() {
    when(this.privilegeAuthorizationService.isRemovable(any(String.class))).thenReturn(true);

    when(this.repository.resolveAnchor(any())).thenReturn(Optional.empty());
    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.useCase.removePrivilege(RemovePrivilegeRequest.builder().anchor("anchor").build()),
        "Expected AnchorNotFoundException for non-existing privilege");
  }

  @Test
  void testRemoveReturnsRemovedResultType() {
    when(this.privilegeAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder().id("2").name("test").permission(new TestPermission()).build()));

    RemovePrivilegeResult result =
        this.useCase.removePrivilege(RemovePrivilegeRequest.builder().id("2").build());

    assertTrue(result instanceof RemovePrivilegeResult.Removed, "Expected Removed result type");
  }

  @Test
  void testRemoveReturnsCorrectPrivilegeId() {
    when(this.privilegeAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder().id("2").name("test").permission(new TestPermission()).build()));

    RemovePrivilegeResult result =
        this.useCase.removePrivilege(RemovePrivilegeRequest.builder().id("2").build());

    RemovePrivilegeResult.Removed removed = (RemovePrivilegeResult.Removed) result;
    assertEquals("2", removed.privilegeId(), "Expected privilege ID 2");
  }

  @Test
  void testRemoveBuiltInFullAccessPrivilegeSkipped() {
    RemovePrivilegeResult result =
        this.useCase.removePrivilege(RemovePrivilegeRequest.builder().id("1").build());

    assertTrue(result instanceof RemovePrivilegeResult.Skipped, "Expected Skipped result type");
  }

  @Test
  void testRemoveCallsRepositoryRemove() {
    when(this.privilegeAuthorizationService.isRemovable(any(String.class))).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder().id("2").name("test").permission(new TestPermission()).build()));

    this.useCase.removePrivilege(RemovePrivilegeRequest.builder().id("2").build());

    verify(this.repository).remove("2");
  }
}
