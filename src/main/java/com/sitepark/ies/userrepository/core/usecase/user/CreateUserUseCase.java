package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.audit.AuditLogService;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogEntryFailedException;
import com.sitepark.ies.sharedkernel.audit.CreateAuditLogRequest;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogAction;
import com.sitepark.ies.userrepository.core.domain.value.AuditLogEntityType;
import com.sitepark.ies.userrepository.core.domain.value.Password;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.PasswordHasher;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class CreateUserUseCase {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AssignRolesToUsersUseCase assignRolesToUsersUseCase;
  private final AccessControl accessControl;
  private final ExtensionsNotifier extensionsNotifier;
  private final PasswordHasher passwordHasher;
  private final AuditLogService auditLogService;
  private final Clock clock;

  @Inject
  CreateUserUseCase(
      UserRepository userRepository,
      RoleRepository roleRepository,
      AssignRolesToUsersUseCase assignRolesToUsersUseCase,
      AccessControl accessControl,
      ExtensionsNotifier extensionsNotifier,
      PasswordHasher passwordHasher,
      AuditLogService auditLogService,
      Clock clock) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.assignRolesToUsersUseCase = assignRolesToUsersUseCase;
    this.accessControl = accessControl;
    this.extensionsNotifier = extensionsNotifier;
    this.passwordHasher = passwordHasher;
    this.auditLogService = auditLogService;
    this.clock = clock;
  }

  public String createUser(CreateUserRequest request) {

    this.validateUser(request.user());

    this.checkAccessControl(request.user());

    this.validateAnchor(request.user());

    this.validateLogin(request.user());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("create user: {}", request.user());
    }

    Password hashedPassword = this.hashPassword(request.user().password());

    User userWithIdAndHashPassword = request.user().toBuilder().password(hashedPassword).build();

    String id = this.userRepository.create(userWithIdAndHashPassword);

    List<String> roleIds =
        IdentifierResolver.create(this.roleRepository).resolve(request.roleIdentifiers());

    CreateAuditLogRequest createAuditLogRequest =
        this.buildCreateAuditLogRequest(
            new UserSnapshot(request.user().toBuilder().id(id).build(), roleIds),
            request.auditParentId());
    this.auditLogService.createAuditLog(createAuditLogRequest);

    if (!roleIds.isEmpty()) {
      this.assignRolesToUsersUseCase.assignRolesToUsers(
          AssignRolesToUsersRequest.builder()
              .userIdentifiers(b -> b.id(id))
              .roleIdentifiers(b -> b.ids(roleIds))
              .build());
    }

    this.extensionsNotifier.notifyCreated(userWithIdAndHashPassword.toBuilder().id(id).build());

    return id;
  }

  private void validateUser(User user) {
    if (user.id() != null) {
      throw new IllegalArgumentException("The ID of the user must not be set when creating.");
    }
    if (user.lastName() == null || user.lastName().isBlank()) {
      throw new IllegalArgumentException("The last-name of the user must not be null or empty.");
    }
  }

  private void checkAccessControl(User user) {
    if (!this.accessControl.isUserCreatable()) {
      throw new AccessDeniedException("Not allowed to create user " + user);
    }
  }

  private void validateAnchor(User user) {
    if (user.anchor() != null) {
      Optional<String> anchorOwner = this.userRepository.resolveAnchor(user.anchor());
      anchorOwner.ifPresent(
          owner -> {
            throw new AnchorAlreadyExistsException(user.anchor(), owner);
          });
    }
  }

  private void validateLogin(User user) {
    Optional<String> resolveLogin = this.userRepository.resolveLogin(user.login());
    resolveLogin.ifPresent(
        owner -> {
          throw new LoginAlreadyExistsException(user.login(), owner);
        });
  }

  @Nullable
  private Password hashPassword(Password password) {
    if (password == null) {
      return null;
    }
    return this.passwordHasher.hash(password.getClearText());
  }

  private CreateAuditLogRequest buildCreateAuditLogRequest(
      UserSnapshot snapshot, String auditLogParentId) {

    String forwardData;
    try {
      forwardData = this.auditLogService.serialize(snapshot);
    } catch (IOException e) {
      throw new CreateAuditLogEntryFailedException(
          AuditLogEntityType.USER.name(), snapshot.user().id(), snapshot.user().toDisplayName(), e);
    }

    return new CreateAuditLogRequest(
        AuditLogEntityType.USER.name(),
        snapshot.user().id(),
        snapshot.user().toDisplayName(),
        AuditLogAction.CREATE.name(),
        null,
        forwardData,
        Instant.now(this.clock),
        auditLogParentId);
  }
}
