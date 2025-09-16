package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.Permission;
import java.util.List;
import java.util.Optional;

public interface PrivilegeRepository {

  String create(Privilege privilege);

  void update(Privilege privilege);

  void remove(String id);

  void restore(Privilege privilege);

  Optional<Privilege> get(String id);

  List<Privilege> getAll();

  List<Privilege> getByIds(List<String> ids);

  Optional<String> resolveAnchor(Anchor anchor);

  void validatePermission(Permission permission);
}
