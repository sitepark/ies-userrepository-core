package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.usecase.audit.PrivilegeSnapshot;
import org.jetbrains.annotations.Nullable;

public record RestorePrivilegeRequest(PrivilegeSnapshot data, @Nullable String auditParentId) {}
