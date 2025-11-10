package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveUsersUseCaseTest {

  private UserRepository repository;
  private AccessControl accessControl;
  private AuditLogService auditLogService;
  private Clock fixedClock;

  private RemoveUsersUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock(UserRepository.class);
    RoleAssigner roleAssigner = mock();
    this.accessControl = mock(AccessControl.class);
    this.auditLogService = mock(AuditLogService.class);
    this.fixedClock = Clock.fixed(Instant.parse("2025-06-30T10:00:00Z"), ZoneId.of("UTC"));

    this.useCase =
        new RemoveUsersUseCase(
            this.repository,
            roleAssigner,
            this.accessControl,
            this.auditLogService,
            this.fixedClock);
  }

  @Test
  void testNoIdentifiers() {
    this.useCase.removeUsers(RemoveUsersRequest.builder().build());
    verifyNoInteractions(this.repository, this.accessControl, this.auditLogService);
  }

  @Test
  void testAccessDenied() {
    when(this.accessControl.isUserRemovable()).thenReturn(false);
    assertThrows(
        AccessDeniedException.class,
        () ->
            this.useCase.removeUsers(
                RemoveUsersRequest.builder().identifiers(b -> b.id("2")).build()));
  }

  @Test
  void testRemoveWithId() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(user));

    this.useCase.removeUsers(RemoveUsersRequest.builder().identifiers(b -> b.add("2")).build());

    verify(this.repository).remove("2");
  }

  @Test
  void testRemoveWithAnchorNotFound() {
    when(this.accessControl.isUserRemovable()).thenReturn(true);

    assertThrows(
        AnchorNotFoundException.class,
        () ->
            this.useCase.removeUsers(
                RemoveUsersRequest.builder().identifiers(b -> b.add("myanchor")).build()));
  }

  @Test
  void testRemoveWithAnchor() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.resolveAnchor(any())).thenReturn(Optional.of("2"));
    when(this.repository.get(any())).thenReturn(Optional.of(user));

    this.useCase.removeUsers(
        RemoveUsersRequest.builder().identifiers(b -> b.add("myanchor")).build());

    verify(this.repository).remove("2");
  }

  @Test
  void testAudit() {
    User user = User.builder().id("2").login("test").lastName("test").build();
    when(this.accessControl.isUserRemovable()).thenReturn(true);
    when(this.repository.get(any())).thenReturn(Optional.of(user));

    this.useCase.removeUsers(RemoveUsersRequest.builder().identifiers(b -> b.id("2")).build());

    CreateAuditLogRequest auditCommand =
        new CreateAuditLogRequest(
            AuditLogEntityType.USER.name(),
            user.id(),
            user.toDisplayName(),
            AuditLogAction.REMOVE.name(),
            null,
            null,
            Instant.now(this.fixedClock),
            null);

    verify(this.auditLogService).createAuditLog(auditCommand);
  }
}
