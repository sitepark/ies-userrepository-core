package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.userrepository.core.domain.value.RoleSnapshot;
import org.jetbrains.annotations.Nullable;

public record RestoreRoleRequest(RoleSnapshot data, @Nullable String auditParentId) {}
