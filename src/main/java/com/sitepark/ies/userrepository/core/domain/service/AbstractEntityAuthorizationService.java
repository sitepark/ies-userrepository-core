package com.sitepark.ies.userrepository.core.domain.service;

import com.sitepark.ies.sharedkernel.security.Authentication;
import com.sitepark.ies.sharedkernel.security.EntityAuthorizationService;
import com.sitepark.ies.sharedkernel.security.FullAccess;
import com.sitepark.ies.userrepository.core.domain.value.permission.UserManagementPermission;
import jakarta.inject.Provider;

public abstract class AbstractEntityAuthorizationService implements EntityAuthorizationService {

  private final Provider<Authentication> authenticationProvider;

  public AbstractEntityAuthorizationService(Provider<Authentication> authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  protected UserManagementPermission getUserPermission() {

    Authentication authentication = this.authenticationProvider.get();
    if (authentication == null) {
      return UserManagementPermission.EMPTY;
    }

    return authentication
        .getPermission(UserManagementPermission.class)
        .orElse(UserManagementPermission.EMPTY);
  }

  protected boolean hasFullAccess() {
    Authentication authentication = this.authenticationProvider.get();
    return authentication != null && authentication.hasPermission(FullAccess.class);
  }
}
