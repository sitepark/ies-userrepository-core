package com.sitepark.ies.userrepository.core.domain.value;

import com.sitepark.ies.userrepository.core.domain.entity.User;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public record UserSnapshot(User user, List<String> roleIds) {
  public UserSnapshot {
    roleIds = roleIds != null ? List.copyOf(roleIds) : Collections.emptyList();
  }

  public List<String> roleIds() {
    return List.copyOf(roleIds);
  }
}
