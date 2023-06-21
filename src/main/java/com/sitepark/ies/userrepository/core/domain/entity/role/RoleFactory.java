package com.sitepark.ies.userrepository.core.domain.entity.role;

import java.io.Serializable;

import com.sitepark.ies.userrepository.core.domain.entity.Role;

public interface RoleFactory extends Serializable {
	boolean accept(String role);
	Role create(String role);
}
