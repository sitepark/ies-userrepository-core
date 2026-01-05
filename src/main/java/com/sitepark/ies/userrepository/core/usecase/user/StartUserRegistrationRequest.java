package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.email.ExternalEmailParameters;

public record StartUserRegistrationRequest(String email, ExternalEmailParameters emailParameters) {}
