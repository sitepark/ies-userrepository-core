package com.sitepark.ies.userrepository.core.port;

public interface AccessControl {
  boolean isImpersonationTokensManageable();

  boolean isUserCreateable();

  boolean isUserReadable();

  boolean isUserWritable();

  boolean isUserRemovable();
}
