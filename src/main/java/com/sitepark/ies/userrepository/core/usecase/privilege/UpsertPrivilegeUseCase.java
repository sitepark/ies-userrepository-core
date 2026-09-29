package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
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

  public UpsertPrivilegeResult upsertPrivilege(UpsertPrivilegeRequest request) {

    Privilege privilegeResolved = this.toPrivilegeWithId(request.privilege());
    if (privilegeResolved.id() == null) {
      CreatePrivilegeResult result =
          this.createPrivilegeUseCase.createPrivilege(
              CreatePrivilegeRequest.builder()
                  .privilege(privilegeResolved)
                  .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers().getValue()))
                  .build());
      return UpsertPrivilegeResult.created(result.privilegeId(), result);
    } else {
      UpdatePrivilegeResult result =
          this.updatePrivilegeUseCase.updatePrivilege(
              UpdatePrivilegeRequest.builder()
                  .privilege(privilegeResolved)
                  .roleIdentifiers(b -> b.identifiers(request.roleIdentifiers().getValue()))
                  .build());
      return UpsertPrivilegeResult.updated(privilegeResolved.id(), result);
    }
  }

  private Privilege toPrivilegeWithId(Privilege privilege) {
    Anchor anchor = privilege.anchor();
    if (privilege.id() == null && anchor != null) {
      return this.repository
          .resolveAnchor(anchor)
          .map(s -> privilege.toBuilder().id(s).build())
          .orElse(privilege);
    } else if (privilege.id() != null && anchor != null) {
      this.repository
          .resolveAnchor(anchor)
          .ifPresent(
              owner -> {
                if (!owner.equals(privilege.id())) {
                  throw new AnchorAlreadyExistsException(anchor, owner);
                }
              });
    }
    return privilege;
  }
}
