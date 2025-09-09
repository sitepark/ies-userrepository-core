package com.sitepark.ies.userrepository.core.port;

public interface AccessControl {
  boolean isImpersonationTokensManageable();

  boolean isUserCreatable();

  boolean isUserReadable();

  boolean isUserWritable();

  boolean isUserRemovable();

  boolean isRoleCreatable();

  boolean isRoleReadable();

  boolean isRoleWritable();

  boolean isRoleRemovable();

  boolean isPrivilegeCreatable();

  boolean isPrivilegeReadable();

  boolean isPrivilegeWritable();

  boolean isPrivilegeRemovable();
}
