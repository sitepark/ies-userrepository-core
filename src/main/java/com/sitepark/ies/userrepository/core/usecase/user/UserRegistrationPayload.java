package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.security.CodeVerificationPayload;

public record UserRegistrationPayload(String email) implements CodeVerificationPayload {}
