package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends AnchorResolver {

  String create(Role role);

  void update(Role role);

  void remove(String id);

  void restore(Role role);

  Optional<Role> get(String id);

  List<Role> getAll();

  List<Role> getByIds(List<String> ids);

  List<Role> getByPrivilegeIds(List<String> privilegeIds);

  @Override
  Optional<String> resolveAnchor(Anchor anchor);
}
