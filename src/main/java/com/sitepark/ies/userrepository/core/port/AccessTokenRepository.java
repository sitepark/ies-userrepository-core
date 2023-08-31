package com.sitepark.ies.userrepository.core.port;

import java.util.Optional;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;

public interface AccessTokenRepository {

	AccessToken create(AccessToken accessToken);

	void revoke(long user, long id);

	void purge(long user, long id);

	void purgeByUser(long user);

	void touch(long user, long id);

	Optional<AccessToken> getByToken(String token);
}
