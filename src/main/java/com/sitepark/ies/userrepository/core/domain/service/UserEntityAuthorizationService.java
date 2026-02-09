package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.HashSet;
import java.util.List;

public class UserEntityAuthorizationService extends AbstractEntityAuthorizationService {

  @Inject
  public UserEntityAuthorizationService(Provider<Authentication> authenticationProvider) {
    super(authenticationProvider);
  }

  @Override
  public Class<?> type() {
    return User.class;
  }

  @Override
  public boolean isCreatable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().create();
  }

  @Override
  public boolean isReadable(List<String> ids) {
    return this.isReadable();
  }

  @Override
  public boolean isReadable(String id) {
    return this.isReadable();
  }

  private boolean isReadable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().read();
  }

  @Override
  public boolean isWritable(List<String> id) {
    return this.isWritable();
  }

  @Override
  public boolean isWritable(String id) {
    return this.isWritable();
  }

  private boolean isWritable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().write();
  }

  @Override
  public boolean isRemovable(List<String> id) {
    return this.isRemovable();
  }

  @Override
  public boolean isRemovable(String id) {
    return this.isRemovable();
  }

  private boolean isRemovable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().delete();
  }

  public boolean isAllowedAssignRoleToUser(List<String> roleIds) {

    if (roleIds.isEmpty()) {
      return true;
    }

    if (this.hasFullAccess()) {
      return true;
    }

    if (!this.getUserPermission().userGrant().assignRoles()) {
      return false;
    }

    List<String> allowedRoleIds = this.getUserPermission().userGrant().allowedRoleIds();
    return allowedRoleIds.isEmpty()
        || this.getUserPermission().userGrant().assignRoles()
            && new HashSet<>(allowedRoleIds).containsAll(roleIds);
  }
}
