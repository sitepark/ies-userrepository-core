package com.sitepark.ies.userrepository.core.port;

import java.util.List;

import com.sitepark.ies.userrepository.core.domain.entity.Role;

public interface RoleAssigner {

	void assignRoleToUser(List<Role> roleList, List<Long> userList);

	void reassignRoleToUser(List<Role> roleList, List<Long> userList);

	void revokeRoleFromUser(List<Role> roleList, List<Long> userList);

	void revokeAllRolesFromUser(List<Long> userList);

	List<Role> getRolesAssignByUser(long id);

	List<Long> getUserAssignByRole(Role role);

}