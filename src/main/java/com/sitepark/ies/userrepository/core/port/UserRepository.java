package com.sitepark.ies.userrepository.core.port;

import java.util.Optional;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.Identifier;

public interface UserRepository {
	boolean isGroup(long id);
	boolean isEmptyGroup(long id);
	Optional<User> store(User entity);
	Optional<User> get(long id);
	void removeEntity(long id);
	void removeGroup(long id);
	Optional<Identifier> resolveAnchor(Anchor anchor);
	long resolve(Identifier identifier);
}
