package com.sitepark.ies.userrepository.core.port;

import java.util.Optional;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.User;

public interface UserRepository {
	Optional<User> store(User entity);
	Optional<User> get(long id);
	void removeUser(long id);
	long resolve(Identifier identifier);
}
