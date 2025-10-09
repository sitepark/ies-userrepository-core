package com.sitepark.ies.userrepository.core.usecase.audit.revert.user;

import com.sitepark.ies.sharedkernel.audit.RevertFailedException;
import com.sitepark.ies.sharedkernel.audit.RevertRequest;
import com.sitepark.ies.sharedkernel.patch.PatchDocument;
import com.sitepark.ies.sharedkernel.patch.PatchService;
import com.sitepark.ies.sharedkernel.patch.PatchServiceFactory;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.audit.revert.RevertEntityActionHandler;
import com.sitepark.ies.userrepository.core.usecase.user.UpdateUserRequest;
import com.sitepark.ies.userrepository.core.usecase.user.UpdateUserUseCase;
import jakarta.inject.Inject;

public class RevertUserUpdateActionHandler implements RevertEntityActionHandler {

  private final UpdateUserUseCase updateUserUseCase;

  private final PatchService<User> patchService;

  private final UserRepository repository;

  @Inject
  RevertUserUpdateActionHandler(
      UpdateUserUseCase updateUserUseCase,
      PatchServiceFactory patchServiceFactory,
      UserRepository repository) {
    this.updateUserUseCase = updateUserUseCase;
    this.patchService = patchServiceFactory.createPatchService(User.class);
    this.repository = repository;
  }

  @Override
  public void revert(RevertRequest request) {
    PatchDocument patch = this.patchService.parsePatch(request.backwardData());
    User user =
        this.repository
            .get(request.entityId())
            .orElseThrow(
                () -> new RevertFailedException(request, "User not found: " + request.entityId()));
    User patchedUser = this.patchService.applyPatch(user, patch);
    this.updateUserUseCase.updateUser(UpdateUserRequest.builder().user(patchedUser).build());
  }
}
