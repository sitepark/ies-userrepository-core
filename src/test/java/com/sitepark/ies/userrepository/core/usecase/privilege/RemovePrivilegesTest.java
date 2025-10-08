package com.sitepark.ies.userrepository.core.usecase.privilege;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemovePrivilegesTest {

  private PrivilegeRepository repository;
  private AccessControl accessControl;
  private AuditLogService auditLogService;

  private RemovePrivileges usecase;

  private Clock fixedClock;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    RoleAssigner roleAssigner = mock();
    this.accessControl = mock();
    this.auditLogService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    this.fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());
    this.usecase =
        new RemovePrivileges(repository, roleAssigner, accessControl, auditLogService, fixedClock);
  }

  @Test
  void testEmptyIdentifiers() {
    this.usecase.removePrivileges(RemovePrivilegesRequest.builder().build());
    verify(this.accessControl, never()).isPrivilegeRemovable();
  }

  @Test
  void testAccessDenied() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.removePrivileges(RemovePrivilegesRequest.builder().id("1").build()),
        "Expected AccessDeniedException for removing privileges without permission");
  }

  @Test
  void testAnchorNotFound() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);

    when(this.repository.resolveAnchor(any())).thenReturn(Optional.empty());
    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.usecase.removePrivileges(
                RemovePrivilegesRequest.builder()
                    .identifier(Identifier.ofAnchor("anchor"))
                    .build()),
        "Expected AnchorNotFoundException for non-existing privilege");
  }

  @Test
  void testIdNotFound() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.empty());

    verify(this.repository, never()).remove(any());
  }

  @Test
  void testRemove() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder()
                    .id("2")
                    .name("test")
                    .permission(new Permission("test", null))
                    .build()));
    this.usecase.removePrivileges(RemovePrivilegesRequest.builder().id("2").build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveId1() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    this.usecase.removePrivileges(RemovePrivilegesRequest.builder().id("1").build());

    verify(this.repository, never()).remove(any());
  }

  @Test
  void testAuditLog() throws IOException {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder()
                    .id("2")
                    .name("test")
                    .permission(new Permission("test", null))
                    .build()));
    when(this.auditLogService.serialize(any())).thenReturn("serialized");
    this.usecase.removePrivileges(RemovePrivilegesRequest.builder().id("2").build());

    verify(this.auditLogService)
        .createAuditLog(
            new CreateAuditLogRequest(
                AuditLogEntityType.PRIVILEGE.name(),
                "2",
                "test",
                AuditLogAction.REMOVE.name(),
                "serialized",
                null,
                Instant.now(this.fixedClock),
                null));
  }
}
