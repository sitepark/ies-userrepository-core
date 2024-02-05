package com.sitepark.ies.userrepository.core.port;

import java.util.List;

import com.sitepark.ies.userrepository.core.domain.entity.Role;

public interface RoleAssigner {

	void assignRoleToUser(List<Role> roleList, List<String> userList);

	void reassignRoleToUser(List<Role> roleList, List<String> userList);

	void revokeRoleFromUser(List<Role> roleList, List<String> userList);

	void revokeAllRolesFromUser(List<String> userList);

	List<Role> getRolesAssignByUser(String id);

	List<Long> getUserAssignByRole(Role role);

}