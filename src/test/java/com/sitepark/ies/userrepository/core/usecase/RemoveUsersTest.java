package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveUsersTest {

  private UserRepository repository;
  private AccessControl accessControl;
  private AuditLogService auditLogService;
  private Clock fixedClock;

  private RemoveUsers useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(UserRepository.class);
    this.accessControl = mock(AccessControl.class);
    this.auditLogService = mock(AuditLogService.class);
    this.fixedClock = Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

    this.useCase =
        new RemoveUsers(this.repository, this.accessControl, this.auditLogService, this.fixedClock);
  }

  @Test
  void testNoIdentifiers() {
    this.useCase.removeUsers(java.util.Collections.emptyList());
    verifyNoInteractions(this.repository, this.accessControl, this.auditLogService);
  }

  @Test
  void testAccessDenied() {
    List<Identifier> identifiers = List.of(Identifier.ofString("1"));

    when(this.accessControl.isUserRemovable()).thenReturn(false);

    assertThrows(AccessDeniedException.class, () -> this.useCase.removeUsers(identifiers));
  }

  @Test
  void testRemoveWithId() {
    List<Identifier> identifiers = List.of(Identifier.ofString("1"));

    User user = User.builder().id("1").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(user));

    this.useCase.removeUsers(identifiers);

    verify(this.repository).remove("1");
  }

  @Test
  void testRemoveWithAnchorNotFound() {
    List<Identifier> identifiers = List.of(Identifier.ofAnchor("myanchor"));

    when(this.accessControl.isUserRemovable()).thenReturn(true);

    assertThrows(AnchorNotFoundException.class, () -> this.useCase.removeUsers(identifiers));
  }

  @Test
  void testRemoveWithAnchor() {
    List<Identifier> identifiers = List.of(Identifier.ofAnchor("myanchor"));

    User user = User.builder().id("1").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("1"));
    when(this.repository.get(any())).thenReturn(Optional.of(user));

    this.useCase.removeUsers(identifiers);

    verify(this.repository).remove("1");
  }

  @Test
  void testAudit() {
    List<Identifier> identifiers = List.of(Identifier.ofString("1"));

    User user = User.builder().id("1").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(user));

    this.useCase.removeUsers(identifiers);

    CreateAuditLogRequest auditCommand =
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            user.id(),
            AuditLogAction.REMOVE.name(),
            null,
            null,
            Instant.now(this.fixedClock),
            null);

    verify(this.auditLogService).createAuditLog(auditCommand);
  }
}
