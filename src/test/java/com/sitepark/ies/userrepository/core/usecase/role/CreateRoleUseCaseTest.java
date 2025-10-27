package com.sitepark.ies.userrepository.core.usecase.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateRoleUseCaseTest {

  private RoleRepository roleRepository;
  private AssignPrivilegesToRolesUseCase assignPrivilegesToRolesUseCase;
  private AccessControl accessControl;
  private CreateRoleUseCase userCase;

  @BeforeEach
  void setUp() {
    this.roleRepository = mock();
    PrivilegeRepository privilegeRepository = mock();
    this.assignPrivilegesToRolesUseCase = mock();
    this.accessControl = mock();
    AuditLogService auditLogService = mock();
    OffsetDateTime fixedTime = OffsetDateTime.parse("2024-06-13T12:00:00+02:00");
    Clock fixedClock = Clock.fixed(fixedTime.toInstant(), fixedTime.getOffset());

    this.userCase =
        new CreateRoleUseCase(
            this.roleRepository,
            privilegeRepository,
            this.assignPrivilegesToRolesUseCase,
            this.accessControl,
            auditLogService,
            fixedClock);
  }

  @Test
  void testValidateRoleWithId() {
    Role role = Role.builder().id("123").name("test").build();
    assertThrows(
        IllegalArgumentException.class,
        () -> this.userCase.createRole(CreateRoleRequest.builder().role(role).build()),
        "role with id should't be allowed");
  }

  @Test
  void testCheckAccessControlRoleCreatable() {

    when(this.accessControl.isRoleCreatable()).thenReturn(false);

    Role role = Role.builder().name("test").build();
    assertThrows(
        AccessDeniedException.class,
        () -> this.userCase.createRole(CreateRoleRequest.builder().role(role).build()),
        "creating role should be denied");
  }

  @Test
  void testValidateAnchor() {
    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.of("111"));
    Role role = Role.builder().name("test").anchor("test").build();
    assertThrows(
        AnchorAlreadyExistsException.class,
        () -> this.userCase.createRole(CreateRoleRequest.builder().role(role).build()),
        "anchor should already exist");
  }

  @Test
  void testCreate() {
    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.roleRepository.create(any())).thenReturn("12");
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.empty());

    Role role = Role.builder().name("test").build();
    this.userCase.createRole(
        CreateRoleRequest.builder().role(role).privilegeIdentifiers(b -> b.id("123")).build());

    verify(this.roleRepository).create(role);
  }

  @Test
  void testReassignPrivilegesToRoles() {
    when(this.accessControl.isRoleCreatable()).thenReturn(true);
    when(this.accessControl.isRoleWritable()).thenReturn(true);
    when(this.roleRepository.create(any())).thenReturn("12");
    when(this.roleRepository.resolveAnchor(any())).thenReturn(Optional.empty());

    Role role = Role.builder().name("test").build();
    this.userCase.createRole(
        CreateRoleRequest.builder().role(role).privilegeIdentifiers(b -> b.id("123")).build());

    verify(this.assignPrivilegesToRolesUseCase)
        .assignPrivilegesToRoles(
            AssignPrivilegesToRolesRequest.builder()
                .roleIdentifiers(b -> b.id("12"))
                .privilegeIdentifiers(b -> b.id("123"))
                .build());
  }
}
