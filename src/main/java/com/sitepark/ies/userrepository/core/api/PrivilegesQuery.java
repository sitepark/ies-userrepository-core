package com.sitepark.ies.userrepository.core.api;

import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.List;
import java.util.Map;

public interface PrivilegesQuery {
  List<Privilege> getPrivilegesByUserId(String userId);

  List<String> getRolesAssignByPrivilege(String privilegeId);

  Map<String, List<String>> getRolesAssignByPrivileges(List<String> privilegeIds);

  List<Privilege> getPrivilegesByDataField(String jsonPath, List<String> fieldValues);
}
