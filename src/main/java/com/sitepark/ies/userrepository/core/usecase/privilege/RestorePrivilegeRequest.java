package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.userrepository.core.domain.value.PrivilegeSnapshot;

public record RestorePrivilegeRequest(PrivilegeSnapshot data) {}
