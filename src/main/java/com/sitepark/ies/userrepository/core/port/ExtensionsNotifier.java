package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.User;

public interface ExtensionsNotifier {
	void notifyPurge(long id);
	void notifyCreated(User user);
	void notifyUpdated(User user);
}
