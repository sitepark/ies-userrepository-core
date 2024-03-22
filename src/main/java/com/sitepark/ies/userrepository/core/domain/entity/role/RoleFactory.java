package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.io.Serializable;

/**
 * The <code>RoleFactory</code> interface defines the contract for classes
 * responsible for creating role-related objects. Classes that implement
 * this interface, such as {@link RefFactory}, provide methods for creating
 * and managing roles within the system.
 */
public interface RoleFactory extends Serializable {
  boolean accept(String role);

  Role create(String role);
}
