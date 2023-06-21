package com.sitepark.ies.userrepository.core.port;

import java.util.Optional;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;

public interface UserRepository {
	void create(User entity);
	void update(User entity);
	Optional<User> get(long id);
	void remove(long id);
	Optional<Long> resolveLogin(String login);
	Optional<Long> resolveAnchor(Anchor anchor);
}
