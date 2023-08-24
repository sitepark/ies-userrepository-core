package com.sitepark.ies.userrepository.core.port;

public interface AccessControl {
	boolean isImpersonationTokensManageable();
	boolean isUserCreateable();
	boolean isUserReadable(long id);
	boolean isUserWritable(long id);
	boolean isUserRemovable(long id);
}
