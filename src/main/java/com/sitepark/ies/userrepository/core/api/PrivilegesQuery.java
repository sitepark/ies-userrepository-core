package com.sitepark.ies.userrepository.core.api;

import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import java.util.List;

public interface PrivilegesQuery {
  List<Privilege> getPrivilegesByUserId(String userId);

  List<Privilege> getPrivilegesByDataField(String jsonPath, List<String> fieldValues);
}
