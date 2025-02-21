package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import java.util.List;

public interface RoleAssigner {

  void assignRoleToUser(List<Identifier> roleList, List<String> userList);

  void reassignRoleToUser(List<Identifier> roleList, List<String> userList);

  void revokeRoleFromUser(List<Identifier> roleList, List<String> userList);

  void revokeAllRolesFromUser(List<String> userList);

  List<Identifier> getRolesAssignByUser(String id);

  List<String> getUserAssignByRole(Identifier role);
}
