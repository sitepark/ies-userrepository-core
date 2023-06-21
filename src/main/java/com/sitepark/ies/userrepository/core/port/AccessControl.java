package com.sitepark.ies.userrepository.core.port;

public interface AccessControl {
	boolean isUserCreateable();
	boolean isUserReadable(long id);
	boolean isUserWritable(long id);
	boolean isUserRemovable(long id);
}
