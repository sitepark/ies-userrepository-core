package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.HashSet;
import java.util.List;

public class RoleEntityAuthorizationService extends AbstractEntityAuthorizationService {

  @Inject
  public RoleEntityAuthorizationService(Provider<Authentication> authenticationProvider) {
    super(authenticationProvider);
  }

  @Override
  public Class<?> type() {
    return Role.class;
  }

  @Override
  public boolean isCreatable() {
    return this.hasFullAccess() || this.getUserPermission().roleGrant().create();
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
    return this.hasFullAccess() || this.getUserPermission().roleGrant().read();
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
    return this.hasFullAccess() || this.getUserPermission().roleGrant().write();
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
    return this.hasFullAccess() || this.getUserPermission().roleGrant().delete();
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
        || this.getUserPermission().roleGrant().assignPrivileges()
            && new HashSet<>(allowedPrivilegeIds).containsAll(privilegesIds);
  }
}
