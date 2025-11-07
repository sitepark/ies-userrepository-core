package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.sharedkernel.security.FullAccess;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserManagementPermission;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.HashSet;
import java.util.List;

public class AccessControl {

  private final Provider<Authentication> authenticationProvider;

  @Inject
  public AccessControl(Provider<Authentication> authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  public boolean isUserCreateable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().create();
  }

  public boolean isUserReadable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().read();
  }

  public boolean isUserWritable() {
    return this.hasFullAccess() || this.getUserPermission().userGrant().write();
  }

  public boolean isUserRemovable() {
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

  public boolean isAllowedAssignPrivilegesToRole(List<String> privilegesIds) {

    if (privilegesIds.isEmpty()) {
      return true;
    }

    if (this.hasFullAccess()) {
      return true;
    }

    if (!this.getUserPermission().roleGrant().assignPrivileges()) {
      return false;
    }

    List<String> allowedPrivilegeIds = this.getUserPermission().roleGrant().allowedPrivilegeIds();
    return allowedPrivilegeIds.isEmpty()
        || this.getUserPermission().userGrant().assignRoles()
            && new HashSet<>(allowedPrivilegeIds).containsAll(privilegesIds);
  }

  private UserManagementPermission getUserPermission() {

    Authentication authentication = this.authenticationProvider.get();
    if (authentication == null) {
      return UserManagementPermission.EMPTY;
    }

    return authentication
        .getPermission(UserManagementPermission.class)
        .orElse(UserManagementPermission.EMPTY);
  }

  private boolean hasFullAccess() {

    Authentication authentication = this.authenticationProvider.get();
    if (authentication == null) {
      return false;
    }

    return authentication.hasPermission(FullAccess.class);
  }
}
