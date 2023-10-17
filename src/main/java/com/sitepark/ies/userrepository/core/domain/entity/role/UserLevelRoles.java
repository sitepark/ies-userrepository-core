package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.sitepark.ies.userrepository.core.domain.entity.Role;

/**
 * The <code>UserLevelRoles</code> class defines a set of predefined user-level roles
 * within the application. These roles, including ADMINISTRATOR, USER, and EXTERNAL,
 * are used to manage user permissions and access control.
 */
public final class UserLevelRoles {

	/**
	 * The <code>ADMINISTRATOR</code> role represents a user with
	 * full system access, allowing complete control and administration
	 * of the application.
	 */
	public static final Role ADMINISTRATOR = Role.ofName("ADMINISTRATOR");

	/**
	 * The <code>USER</code> role represents a standard user, and
	 * their roles define their access permissions within the system.
	 */
	public static final Role USER = Role.ofName("USER");

	/**
	 * The <code>EXTERNAL</code> role represents users marked as external,
	 * subject to certain limitations. These users are restricted from
	 * accessing backend applications.
	 */
	public static final Role EXTERNAL = Role.ofName("EXTERNAL");

	private UserLevelRoles() {}

	public static boolean isUserLevelRole(Role role) {
		return
					ADMINISTRATOR.equals(role) ||
					USER.equals(role) ||
					EXTERNAL.equals(role);
	}
}
