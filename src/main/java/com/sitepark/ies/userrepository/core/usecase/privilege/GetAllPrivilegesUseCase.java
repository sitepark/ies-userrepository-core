package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.domain.service.PrivilegeEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;
import java.util.List;

public final class GetAllPrivilegesUseCase {

  private final PrivilegeRepository repository;

  private final PrivilegeEntityAuthorizationService privilegeAuthorizationService;

  @Inject
  GetAllPrivilegesUseCase(
      PrivilegeRepository repository,
      PrivilegeEntityAuthorizationService privilegeAuthorizationService) {
    this.repository = repository;
    this.privilegeAuthorizationService = privilegeAuthorizationService;
  }

  public List<Privilege> getAllPrivileges() {

    List<Privilege> privileges = this.repository.getAll();

    if (!this.privilegeAuthorizationService.isReadable(
        privileges.stream().map(Privilege::id).toList())) {
      throw new AccessDeniedException("Not allowed to read privileges");
    }

    return privileges;
  }
}
