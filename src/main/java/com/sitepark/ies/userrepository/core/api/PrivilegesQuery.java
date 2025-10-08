package com.sitepark.ies.userrepository.core.api;

import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.value.PrivilegeRoleAssignment;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import java.util.List;

public interface PrivilegesQuery {
  List<Privilege> getPrivilegesByUserId(String userId);

  List<String> getRolesAssignByPrivilege(String privilegeId);

  PrivilegeRoleAssignment getRolesAssignByPrivileges(List<String> privilegeIds);

  RolePrivilegeAssignment getPrivilegesAssignByRoles(List<String> roleIds);

  List<Privilege> getPrivilegesByDataField(String jsonPath, List<String> fieldValues);
}
