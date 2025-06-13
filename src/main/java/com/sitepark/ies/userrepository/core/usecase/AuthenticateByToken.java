package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenExpiredException;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenNotActiveException;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenRevokedException;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAccessTokenException;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Optional;

public class AuthenticateByToken {

  private final AccessTokenRepository accessTokenRepository;

  private final UserRepository userRepository;

  @Inject
  protected AuthenticateByToken(
      AccessTokenRepository accessTokenRepository, UserRepository userRepository) {
    this.accessTokenRepository = accessTokenRepository;
    this.userRepository = userRepository;
  }

  public User authenticateByToken(String token) {

    AccessToken accessToken = getValidAccessToken(token);
    validateAccessToken(accessToken);

    Optional<User> user = this.userRepository.get(accessToken.getUser());
    if (user.isEmpty()) {
      throw new InvalidAccessTokenException("User " + accessToken.getUser() + " not found");
    }

    return user.get();
  }

  private AccessToken getValidAccessToken(String token) {
    Optional<AccessToken> accessTokenOptional = this.accessTokenRepository.getByToken(token);
    if (accessTokenOptional.isEmpty()) {
      throw new InvalidAccessTokenException("Token not found");
    }
    return accessTokenOptional.get();
  }

  private void validateAccessToken(AccessToken accessToken) {
    if (!accessToken.isActive()) {
      throw new AccessTokenNotActiveException();
    }
    if (accessToken.isRevoked()) {
      throw new AccessTokenRevokedException();
    }
    if (accessToken.getExpiresAt() != null) {
      this.checkExpirationDate(accessToken.getExpiresAt());
    }
  }

  public void checkExpirationDate(OffsetDateTime expiredAt) {

    OffsetDateTime now = OffsetDateTime.now();
    if (expiredAt.isBefore(now)) {
      throw new AccessTokenExpiredException(expiredAt);
    }
  }
}
