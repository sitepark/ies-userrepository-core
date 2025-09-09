package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {

  String create(Role role);

  void update(Role role);

  void remove(List<String> ids);

  Optional<Role> get(String id);

  List<Role> getAll();

  List<Role> getByIds(List<String> ids);

  List<Role> getByPrivilegeIds(List<String> privilegeIds);

  Optional<String> resolveAnchor(Anchor anchor);
}
