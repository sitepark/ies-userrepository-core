package com.sitepark.ies.userrepository.core.port;

import java.util.List;

public interface RoleAssigner {

  void assignUsersToRoles(List<String> rolesIds, List<String> userIds);

  void reassignUsersToRoles(List<String> roleIds, List<String> userIds);

  void revokeUsersFromRoles(List<String> roleIds, List<String> userIds);

  void revokeAllRolesFromUsers(List<String> userIds);

  void revokeAllUsersFromRoles(List<String> rolesIds);

  void assignPrivilegesToRoles(List<String> rolesIds, List<String> privilegeIds);

  void reassignPrivilegesToRoles(List<String> rolesIds, List<String> privilegeIds);

  void revokePrivilegesFromRoles(List<String> roleIds, List<String> privilegeIds);

  void revokeAllPrivilegesFromRoles(List<String> roleIds);

  void revokeAllRolesFromPrivileges(List<String> privilegeIds);

  List<String> getRolesAssignByUser(String userId);

  List<String> getUserAssignByRole(String roleId);
}
