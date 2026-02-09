package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetPrivilegesByIdsUseCase {

  private final PrivilegeRepository repository;

  private final PrivilegeEntityAuthorizationService privilegeAuthorizationService;

  @Inject
  GetPrivilegesByIdsUseCase(
      PrivilegeRepository repository,
      PrivilegeEntityAuthorizationService privilegeAuthorizationService) {
    this.repository = repository;
    this.privilegeAuthorizationService = privilegeAuthorizationService;
  }

  public List<Privilege> getPrivilegesByIds(List<String> ids) {

    if (!this.privilegeAuthorizationService.isReadable(ids)) {
      throw new AccessDeniedException("Not allowed to read privileges");
    }

    return this.repository.getByIds(ids);
  }
}
