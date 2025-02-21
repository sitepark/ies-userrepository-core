package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {
  void create(Role entity);

  void update(Role entity);

  Optional<Role> get(String id);

  List<Role> getAll();

  void remove(String id);

  Optional<String> resolveAnchor(Anchor anchor);
}
