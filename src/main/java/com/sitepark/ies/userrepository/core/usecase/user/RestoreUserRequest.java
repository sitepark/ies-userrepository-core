package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.userrepository.core.usecase.audit.UserSnapshot;
import org.jetbrains.annotations.Nullable;

public record RestoreUserRequest(UserSnapshot data, @Nullable String auditParentId) {}
