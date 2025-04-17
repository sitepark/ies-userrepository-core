package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.domain.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.List;
import java.util.Optional;

public interface PrivilegeRepository {
  void create(Privilege entity);

  void update(Privilege entity);

  Optional<Privilege> get(String id);

  List<Privilege> getAll();

  List<Privilege> getByIds(List<String> ids);

  void remove(String id);

  Optional<String> resolveAnchor(Anchor anchor);
}
