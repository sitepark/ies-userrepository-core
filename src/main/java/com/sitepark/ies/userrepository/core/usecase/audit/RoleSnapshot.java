package com.sitepark.ies.userrepository.core.usecase.audit;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public record RoleSnapshot(Role role, List<String> userIds, List<String> privilegesIds) {
  public RoleSnapshot {
    userIds = userIds != null ? List.copyOf(userIds) : Collections.emptyList();
    privilegesIds = privilegesIds != null ? List.copyOf(privilegesIds) : Collections.emptyList();
  }

  public List<String> userIds() {
    return List.copyOf(userIds);
  }

  public List<String> privilegesIds() {
    return List.copyOf(privilegesIds);
  }
}
