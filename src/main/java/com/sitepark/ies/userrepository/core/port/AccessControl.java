package com.sitepark.ies.userrepository.core.port;

public interface AccessControl {
  boolean isImpersonationTokensManageable();

  boolean isUserCreateable();

  boolean isUserReadable(String id);

  boolean isUserWritable(String id);

  boolean isUserRemovable(String id);
}
