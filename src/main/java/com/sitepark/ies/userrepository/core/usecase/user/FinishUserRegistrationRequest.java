package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.email.ExternalEmailParameters;
import com.sitepark.ies.userrepository.core.domain.entity.GenderType;
import java.util.List;

public record FinishUserRegistrationRequest(
    String challengeId,
    int code,
    String firstName,
    String lastName,
    GenderType gender,
    String password,
    List<Identifier> roleIdentifiers,
    ExternalEmailParameters emailParameters) {

  public FinishUserRegistrationRequest {
    roleIdentifiers = roleIdentifiers == null ? List.of() : List.copyOf(roleIdentifiers);
  }

  public List<Identifier> roleIdentifiers() {
    return List.copyOf(roleIdentifiers);
  }
}
