package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogCommand;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
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
    this.accessControl = mock();
    this.auditLogService = mock();

    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    this.fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());
    this.usecase = new RemovePrivileges(repository, accessControl, auditLogService, fixedClock);
  }

  @Test
  void testEmptyIdentifiers() {
    this.usecase.removePrivileges(List.of());
    verify(this.accessControl, never()).isPrivilegeRemovable();
  }

  @Test
  void testAccessDenied() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () -> this.usecase.removePrivileges(List.of(Identifier.ofId("1"))),
        "Expected AccessDeniedException for removing privileges without permission");
  }

  @Test
  void testAnchorNotFound() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);

    when(this.repository.resolveAnchor(any())).thenReturn(Optional.empty());
    assertThrows(
        AnchorNotFoundException.class,
        () -> this.usecase.removePrivileges(List.of(Identifier.ofAnchor("anchor"))),
        "Expected AnchorNotFoundException for non-existing privilege");
  }

  @Test
  void testIdNotFound() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.empty());
    assertThrows(
        IllegalArgumentException.class,
        () -> this.usecase.removePrivileges(List.of(Identifier.ofId("1"))),
        "Expected IllegalArgumentException for non-existing id");
  }

  @Test
  void testRemove() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder()
                    .id("1")
                    .name("test")
                    .permission(new Permission("test", null))
                    .build()));
    this.usecase.removePrivileges(List.of(Identifier.ofId("1")));

    verify(this.repository).remove(List.of("1"));
  }

  @Test
  void testAuditLog() {
    when(this.accessControl.isPrivilegeRemovable()).thenReturn(true);
    when(this.repository.get(any()))
        .thenReturn(
            Optional.of(
                Privilege.builder()
                    .id("1")
                    .name("test")
                    .permission(new Permission("test", null))
                    .build()));
    this.usecase.removePrivileges(List.of(Identifier.ofId("1")));

    verify(this.auditLogService)
        .createAuditLog(
            new CreateAuditLogCommand(
                "Removed privileges",
                "privilege",
                "1",
                "remove",
                "{}",
                "{}",
                Instant.now(this.fixedClock),
                null));
  }
}
