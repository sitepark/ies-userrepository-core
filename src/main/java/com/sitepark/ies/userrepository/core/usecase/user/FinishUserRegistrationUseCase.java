package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.email.Email;
import com.sitepark.ies.sharedkernel.email.EmailAddress;
import com.sitepark.ies.sharedkernel.email.EmailMessageTypeIdentifier;
import com.sitepark.ies.sharedkernel.email.EmailSendException;
import com.sitepark.ies.sharedkernel.email.EmailService;
import com.sitepark.ies.sharedkernel.email.ExternalEmailParameters;
import com.sitepark.ies.sharedkernel.email.TemplateEmailMessage;
import com.sitepark.ies.sharedkernel.security.CodeVerificationChallenge;
import com.sitepark.ies.sharedkernel.security.CodeVerificationFailedException;
import com.sitepark.ies.sharedkernel.security.CodeVerificationService;
import com.sitepark.ies.userrepository.core.domain.entity.GenderType;
import com.sitepark.ies.userrepository.core.domain.entity.Password;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.exception.FinishUserRegistrationException;
import com.sitepark.ies.userrepository.core.usecase.CreateUser;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FinishUserRegistrationUseCase {

  public static final EmailMessageTypeIdentifier USER_REGISTRATION_CONFIRMATION =
      new EmailMessageTypeIdentifier("notification", "user-registration-confirmation");

  private final CodeVerificationService codeVerificationService;

  private final CreateUser createUser;

  private final EmailService emailService;

  @Inject
  FinishUserRegistrationUseCase(
      CodeVerificationService codeVerificationService,
      CreateUser createUser,
      EmailService emailService) {
    this.codeVerificationService = codeVerificationService;
    this.createUser = createUser;
    this.emailService = emailService;
  }

  public FinishUserRegistrationResult finishUserRegistration(
      FinishUserRegistrationRequest request) {

    CodeVerificationChallenge challenge =
        this.codeVerificationService.finishChallenge(request.challengeId(), request.code());

    if (!(challenge.payload() instanceof UserRegistrationPayload(String email))) {
      throw new CodeVerificationFailedException("Payload is not UserRegistrationPayload");
    }

    List<Role> roles = request.roleIdentifiers().stream().map(this::toRole).toList();

    User user =
        User.builder()
            .login(email)
            .email(email)
            .firstName(request.firstName())
            .lastName(request.lastName())
            .gender(request.gender() != null ? request.gender() : GenderType.UNKNOWN)
            .roles(roles)
            .password(Password.builder().clearText(request.password()).build())
            .build();

    String id = this.createUser.createUser(user);

    User userForEmail = user.toBuilder().password(null).build();
    this.sendSuccessEmail(request, userForEmail);

    return new FinishUserRegistrationResult(email, id);
  }

  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private Role toRole(Identifier identifier) {
    if (identifier.getId() != null) {
      return Ref.ofId(identifier.getId());
    } else {
      return Ref.ofAnchor(identifier.getAnchor().getName());
    }
  }

  private void sendSuccessEmail(FinishUserRegistrationRequest request, User user) {

    TemplateEmailMessage message = this.createEmailMessage(request.emailParameters(), user);

    Email email =
        Email.builder()
            .from(request.emailParameters().from())
            .replyTo(configurer -> configurer.set(request.emailParameters().replyTo()))
            .to(
                configurer ->
                    configurer.set(EmailAddress.builder().address(user.getEmail().get()).build()))
            .message(message)
            .build();
    try {
      this.emailService.send(email);
    } catch (EmailSendException e) {
      throw new FinishUserRegistrationException("Send email failed", e);
    }
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private TemplateEmailMessage createEmailMessage(ExternalEmailParameters parameters, User user) {
    Map<String, Object> data = new HashMap<>();
    data.put("user", user);

    return TemplateEmailMessage.builder()
        .messageType(USER_REGISTRATION_CONFIRMATION)
        .theme(parameters.theme())
        .lang(parameters.lang())
        .data(configurer -> configurer.putAll(data))
        .build();
  }
}
