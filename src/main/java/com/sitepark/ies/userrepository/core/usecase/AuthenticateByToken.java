package com.sitepark.ies.userrepository.core.usecase;

import java.time.OffsetDateTime;
import java.util.Optional;

import jakarta.inject.Inject;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenExpiredException;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenNotActiveException;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenRevokedException;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAccessTokenException;
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
			throw new InvalidAccessTokenException("Token not found");
		}

		AccessToken accessToken = accessTokenOptinal.get();
		if (!accessToken.isActive()) {
			throw new AccessTokenNotActiveException();
		}
		if (accessToken.isRevoked()) {
			throw new AccessTokenRevokedException();
		}
		this.checkExpirationDate(accessToken.getExpiresAt());

		Optional<User> user = this.userRepository.get(accessToken.getUser());
		if (user.isEmpty()) {
			throw new InvalidAccessTokenException("User " + accessToken.getUser() + " not found");
		}

		return user.get();
	}

	public void checkExpirationDate(Optional<OffsetDateTime> expiredAt) {

		if (expiredAt.isEmpty()) {
			return;
		}

		OffsetDateTime now = OffsetDateTime.now();
		if (expiredAt.get().isBefore(now)) {
			throw new AccessTokenExpiredException(expiredAt.get());
		}
	}
}
