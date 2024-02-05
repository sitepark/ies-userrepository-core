package com.sitepark.ies.userrepository.core.port;

import java.util.Optional;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;

public interface AccessTokenRepository {

	AccessToken create(AccessToken accessToken);

	void revoke(String user, String id);

	void purge(String user, String id);

	void purgeByUser(String user);

	void touch(String user, String id);

	Optional<AccessToken> getByToken(String token);
}
