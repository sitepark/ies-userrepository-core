package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.security.Permission;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestPermission implements Permission {

  public static final String TYPE = "TEST";

  @Override
  public String getType() {
    return TYPE;
  }
}
