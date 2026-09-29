package com.sitepark.ies.userrepository.core.domain.value;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.Collections;
import java.util.List;

public record RoleSnapshot(Role role, List<String> userIds, List<String> privilegesIds) {
  public RoleSnapshot {
    userIds = userIds != null ? List.copyOf(userIds) : Collections.emptyList();
    privilegesIds = privilegesIds != null ? List.copyOf(privilegesIds) : Collections.emptyList();
  }

  @Override
  public List<String> userIds() {
    return List.copyOf(userIds);
  }

  @Override
  public List<String> privilegesIds() {
    return List.copyOf(privilegesIds);
  }
}
