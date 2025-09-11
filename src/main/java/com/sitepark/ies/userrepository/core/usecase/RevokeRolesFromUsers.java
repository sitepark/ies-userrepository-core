package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.sharedkernel.anchor.AnchorNotFoundException;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.RoleRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RevokeRolesFromUsers {

  private static final Logger LOGGER = LogManager.getLogger();
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleAssigner roleAssigner;
  private final AccessControl accessControl;

  @Inject
  RevokeRolesFromUsers(
      UserRepository userRepository,
      RoleRepository roleRepository,
      RoleAssigner roleAssigner,
      AccessControl accessControl) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.roleAssigner = roleAssigner;
    this.accessControl = accessControl;
  }

  public void revokeRolesFromUsers(
      @NotNull List<Identifier> userIdentifiers, @NotNull List<Identifier> roleIdentifiers) {

    if (userIdentifiers.isEmpty() || roleIdentifiers.isEmpty()) {
      return;
    }

    List<String> userIds =
        userIdentifiers.stream()
            .map(
                user ->
                    user.resolveId(
                        (anchor) ->
                            this.userRepository
                                .resolveAnchor(user.getAnchor())
                                .orElseThrow(() -> new AnchorNotFoundException(anchor))))
            .toList();

    List<String> roleIds =
        roleIdentifiers.stream()
            .map(
                role ->
                    role.resolveId(
                        (anchor) ->
                            this.roleRepository
                                .resolveAnchor(role.getAnchor())
                                .orElseThrow(() -> new AnchorNotFoundException(anchor))))
            .toList();

    if (!this.accessControl.isUserWritable()) {
      throw new AccessDeniedException("Not allowed to update user to add roles");
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("assign roles to users({}) -> roles({})", userIds, roleIdentifiers);
    }

    this.roleAssigner.revokeRolesFromUsers(userIds, roleIds);
  }
}
