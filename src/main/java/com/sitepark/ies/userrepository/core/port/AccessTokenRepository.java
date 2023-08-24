package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;

public interface AccessTokenRepository {

	AccessToken create(AccessToken accessToken);

	void revoke(long user, long id);

}
