package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.List;

public class PrivilegeEntityAuthorizationService extends AbstractEntityAuthorizationService {

  @Inject
  public PrivilegeEntityAuthorizationService(Provider<Authentication> authenticationProvider) {
    super(authenticationProvider);
  }

  @Override
  public Class<?> type() {
    return Privilege.class;
  }

  @Override
  public boolean isCreatable() {
    return this.hasFullAccess() || this.getUserPermission().privilegeGrant().create();
  }

  @Override
  public boolean isReadable(List<String> ids) {
    return isReadable();
  }

  @Override
  public boolean isReadable(String id) {
    return isReadable();
  }

  private boolean isReadable() {
    return this.hasFullAccess() || this.getUserPermission().privilegeGrant().read();
  }

  @Override
  public boolean isWritable(List<String> id) {
    return isWritable();
  }

  @Override
  public boolean isWritable(String id) {
    return isWritable();
  }

  private boolean isWritable() {
    return this.hasFullAccess() || this.getUserPermission().privilegeGrant().write();
  }

  @Override
  public boolean isRemovable(List<String> id) {
    return false;
  }

  @Override
  public boolean isRemovable(String id) {
    return this.isRemovable();
  }

  private boolean isRemovable() {
    return this.hasFullAccess() || this.getUserPermission().privilegeGrant().delete();
  }
}
