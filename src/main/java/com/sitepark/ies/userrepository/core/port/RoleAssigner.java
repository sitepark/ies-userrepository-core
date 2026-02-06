package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.value.PrivilegeRoleAssignment;
import com.sitepark.ies.userrepository.core.domain.value.RolePrivilegeAssignment;
import com.sitepark.ies.userrepository.core.domain.value.RoleUserAssignment;
import com.sitepark.ies.userrepository.core.domain.value.UserRoleAssignment;
import java.util.List;

public interface RoleAssigner {

  // user-role

  void assignRolesToUsers(List<String> userIds, List<String> roleIds);

  void unassignRolesFromUsers(List<String> userIds, List<String> roleIds);

  void unassignAllRolesFromUsers(List<String> userIds);

  void unassignAllUsersFromRoles(List<String> roleIds);

  // role-privilege

  void assignPrivilegesToRoles(List<String> roleIds, List<String> privilegeIds);

  void reassignPrivilegesToRoles(List<String> roleIds, List<String> privilegeIds);

  void unassignPrivilegesFromRoles(List<String> roleIds, List<String> privilegeIds);

  void unassignAllPrivilegesFromRoles(List<String> roleIds);

  void unassignAllRolesFromPrivileges(List<String> privilegeIds);

  // getter

  List<String> getRolesAssignByUser(String userId);

  List<String> getUsersAssignByRole(String roleId);

  List<String> getPrivilegesAssignByRole(String roleId);

  List<String> getRolesAssignByPrivilege(String privilegeId);

  PrivilegeRoleAssignment getRolesAssignByPrivileges(List<String> privilegeIds);

  RolePrivilegeAssignment getPrivilegesAssignByRoles(List<String> roleIds);

  RoleUserAssignment getUsersAssignByRoles(List<String> rolesIds);

  UserRoleAssignment getRolesAssignByUsers(List<String> userIds);
}
