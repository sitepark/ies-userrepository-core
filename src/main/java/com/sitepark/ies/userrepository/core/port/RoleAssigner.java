package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.util.List;

public interface RoleAssigner {

  void assignRoleToUser(List<Role> roleList, List<String> userList);

  void reassignRoleToUser(List<Role> roleList, List<String> userList);

  void revokeRoleFromUser(List<Role> roleList, List<String> userList);

  void revokeAllRolesFromUser(List<String> userList);

  List<Role> getRolesAssignByUser(String id);

  List<Long> getUserAssignByRole(Role role);
}
