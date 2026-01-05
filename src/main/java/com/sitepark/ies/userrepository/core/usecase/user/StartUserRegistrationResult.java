package com.sitepark.ies.userrepository.core.usecase.user;

import java.time.Instant;

public record StartUserRegistrationResult(
    String challengeId, Instant createdAt, Instant expiresAt) {}
