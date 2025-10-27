package com.sitepark.ies.userrepository.core.usecase.audit.revert;

import com.sitepark.ies.sharedkernel.audit.RevertRequest;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface RevertEntityActionHandler {

  void revert(RevertRequest request);
}
