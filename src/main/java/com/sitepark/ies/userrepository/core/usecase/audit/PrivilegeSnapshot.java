package com.sitepark.ies.userrepository.core.usecase.audit;

import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public record PrivilegeSnapshot(Privilege privilege, List<String> roleIds) {
  public PrivilegeSnapshot {
    roleIds = roleIds != null ? List.copyOf(roleIds) : Collections.emptyList();
  }

  public List<String> roleIds() {
    return List.copyOf(roleIds);
  }
}
