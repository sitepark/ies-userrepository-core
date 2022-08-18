package com.sitepark.ies.userrepository.core.port;

public interface AccessControl {
	boolean isUserCreateable(long parent);
	boolean isUserReadable(long id);
	boolean isUserWritable(long id);
	boolean isUserRemovable(long id);
}
