package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;

public final class UpsertPrivilege {

  private final AccessControl accessControl;
  private final PrivilegeRepository repository;
  private final CreatePrivilege createPrivilegeUseCase;
  private final UpdatePrivilege updatePrivilegeUseCase;

  @Inject
  UpsertPrivilege(
      AccessControl accessControl,
      PrivilegeRepository repository,
      CreatePrivilege createPrivilegeUseCase,
      UpdatePrivilege updatePrivilegeUseCase) {
    this.accessControl = accessControl;
    this.repository = repository;
    this.createPrivilegeUseCase = createPrivilegeUseCase;
    this.updatePrivilegeUseCase = updatePrivilegeUseCase;
  }

  public String upsertPrivilege(UpsertPrivilegeRequest request) {

    if (!this.accessControl.isPrivilegeCreatable() || !this.accessControl.isPrivilegeWritable()) {
      throw new AccessDeniedException("Not allowed to upsert privilege " + request.privilege());
    }

    Privilege privilegeResolved = this.toPrivilegeWithId(request.privilege());
    if (privilegeResolved.id() == null) {
      return this.createPrivilegeUseCase.createPrivilege(
          CreatePrivilegeRequest.builder()
              .privilege(privilegeResolved)
              .roleIds(request.roleIds())
              .build());
    } else {
      return this.updatePrivilegeUseCase.updatePrivilege(
          UpdatePrivilegeRequest.builder()
              .privilege(privilegeResolved)
              .roleIds(request.roleIds())
              .build());
    }
  }

  private Privilege toPrivilegeWithId(Privilege privilege) {
    if (privilege.id() == null && privilege.anchor() != null) {
      return this.repository
          .resolveAnchor(privilege.anchor())
          .map(s -> privilege.toBuilder().id(s).build())
          .orElse(privilege);
    } else if (privilege.id() != null && privilege.anchor() != null) {
      this.repository
          .resolveAnchor(privilege.anchor())
          .ifPresent(
              owner -> {
                if (!owner.equals(privilege.id())) {
                  throw new AnchorAlreadyExistsException(privilege.anchor(), owner);
                }
              });
    }
    return privilege;
  }
}
