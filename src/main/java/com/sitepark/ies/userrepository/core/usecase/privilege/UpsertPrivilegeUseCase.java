package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.AnchorAlreadyExistsException;
import com.sitepark.ies.userrepository.core.domain.entity.Privilege;
import com.sitepark.ies.userrepository.core.port.PrivilegeRepository;
import jakarta.inject.Inject;

public final class UpsertPrivilegeUseCase {

  private final PrivilegeRepository repository;
  private final CreatePrivilegeUseCase createPrivilegeUseCase;
  private final UpdatePrivilegeUseCase updatePrivilegeUseCase;

  @Inject
  UpsertPrivilegeUseCase(
      PrivilegeRepository repository,
      CreatePrivilegeUseCase createPrivilegeUseCase,
      UpdatePrivilegeUseCase updatePrivilegeUseCase) {
    this.repository = repository;
    this.createPrivilegeUseCase = createPrivilegeUseCase;
    this.updatePrivilegeUseCase = updatePrivilegeUseCase;
  }

  public String upsertPrivilege(UpsertPrivilegeRequest request) {

    Privilege privilegeResolved = this.toPrivilegeWithId(request.privilege());
    if (privilegeResolved.id() == null) {
      return this.createPrivilegeUseCase.createPrivilege(
          CreatePrivilegeRequest.builder()
              .privilege(privilegeResolved)
              .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers()))
              .build());
    } else {
      return this.updatePrivilegeUseCase.updatePrivilege(
          UpdatePrivilegeRequest.builder()
              .privilege(privilegeResolved)
              .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers()))
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
