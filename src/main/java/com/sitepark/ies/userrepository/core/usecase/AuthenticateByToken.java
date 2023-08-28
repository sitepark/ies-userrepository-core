package com.sitepark.ies.userrepository.core.usecase;

import java.time.OffsetDateTime;
import java.util.Optional;

import javax.inject.Inject;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenExpired;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenNotActive;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenRevoked;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAccessToken;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public class AuthenticateByToken {

	private final AccessTokenRepository accessTokenRepository;

	private final UserRepository userRepository;

	@Inject
	protected AuthenticateByToken(
			AccessTokenRepository accessTokenRepository,
			UserRepository userRepository) {
		this.accessTokenRepository = accessTokenRepository;
		this.userRepository = userRepository;
	}

	public User authenticateByToken(String token) {

		Optional<AccessToken> accessTokenOptinal = this.accessTokenRepository.getByToken(token);
		if (accessTokenOptinal.isEmpty()) {
			throw new InvalidAccessToken("Token not found");
		}

		AccessToken accessToken = accessTokenOptinal.get();
		if (!accessToken.isActive()) {
			throw new AccessTokenNotActive();
		}
		if (accessToken.isRevoked()) {
			throw new AccessTokenRevoked();
		}
		this.checkExpirationDate(accessToken.getExpiresAt());

		Optional<User> user = this.userRepository.get(accessToken.getUser());
		if (user.isEmpty()) {
			throw new InvalidAccessToken("User " + accessToken.getUser() + " not found");
		}

		return user.get();
	}

	public void checkExpirationDate(Optional<OffsetDateTime> expiredAt) {

		if (expiredAt.isEmpty()) {
			return;
		}

		OffsetDateTime now = OffsetDateTime.now();
		if (expiredAt.get().isBefore(now)) {
			throw new AccessTokenExpired(expiredAt.get());
		}
	}
}
