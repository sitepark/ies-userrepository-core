package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.sitepark.ies.userrepository.core.domain.entity.Role;

public final class UserLevelRoles {
	public static final Role ADMINISTRATOR = Role.ofName("ADMINISTRATOR");
	public static final Role USER = Role.ofName("USER");
	public static final Role EXTERNAL = Role.ofName("EXTERNAL");

	private UserLevelRoles() {}

	public static boolean isUserLevelRole(Role role) {
		return
					ADMINISTRATOR.equals(role) ||
					USER.equals(role) ||
					EXTERNAL.equals(role);
	}
}
