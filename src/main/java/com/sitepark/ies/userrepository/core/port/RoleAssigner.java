package com.sitepark.ies.userrepository.core.port;

import java.util.List;

public interface RoleAssigner {

  // user-role

  void assignRolesToUsers(List<String> userIds, List<String> roleIds);

  void reassignRolesToUsers(List<String> userIds, List<String> roleIds);

  void revokeRolesFromUsers(List<String> userIds, List<String> roleIds);

  void revokeAllRolesFromUsers(List<String> userIds);

  void revokeAllUsersFromRoles(List<String> roleIds);

  // role-privilege

  void assignPrivilegesToRoles(List<String> roleIds, List<String> privilegeIds);

  void reassignPrivilegesToRoles(List<String> roleIds, List<String> privilegeIds);

  void revokePrivilegesFromRoles(List<String> roleIds, List<String> privilegeIds);

  void revokeAllPrivilegesFromRoles(List<String> roleIds);

  void revokeAllRolesFromPrivileges(List<String> privilegeIds);

  // getter

  List<String> getRolesAssignByUser(String userId);

  List<String> getUsersAssignByRole(String roleId);

  List<String> getPrivilegesAssignByRole(String roleId);

  List<String> getRolesAssignByPrivilege(String privilegeId);
}
