package com.sitepark.ies.userrepository.core.port;

import java.util.List;

public interface RoleAssigner {

  void assignRoleToUser(List<String> rolesIds, List<String> userIds);

  void reassignRoleToUser(List<String> roleIds, List<String> userIds);

  void revokeRoleFromUser(List<String> roleIds, List<String> userIds);

  void revokeAllRolesFromUser(List<String> userIds);

  List<String> getRolesAssignByUser(String userId);

  List<String> getUserAssignByRole(String roleId);
}
