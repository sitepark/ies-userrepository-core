package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.domain.value.UserSnapshot;

public record RestoreUserRequest(UserSnapshot data) {}
