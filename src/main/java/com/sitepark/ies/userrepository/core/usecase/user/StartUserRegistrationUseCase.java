package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.email.Email;
import com.sitepark.ies.sharedkernel.email.EmailAddress;
import com.sitepark.ies.sharedkernel.email.EmailMessageTypeIdentifier;
import com.sitepark.ies.sharedkernel.email.EmailSendException;
import com.sitepark.ies.sharedkernel.email.EmailService;
import com.sitepark.ies.sharedkernel.email.ExternalEmailParameters;
import com.sitepark.ies.sharedkernel.email.TemplateEmailMessage;
import com.sitepark.ies.sharedkernel.security.CodeVerificationChallenge;
import com.sitepark.ies.sharedkernel.security.CodeVerificationService;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.StartUserRegistrationException;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class StartUserRegistrationUseCase {

  public static final EmailMessageTypeIdentifier USER_REGISTRATION_SEND_VERIFICATION_CODE =
      new EmailMessageTypeIdentifier("notification", "user-registration-send-verification-code");
  public static final EmailMessageTypeIdentifier USER_REGISTRATION_EMAIL_ALREADY_EXISTS =
      new EmailMessageTypeIdentifier("notification", "user-registration-email-already-exists");

  private final UserRepository repository;

  private final CodeVerificationService codeVerificationService;

  private final EmailService emailService;

  @Inject
  StartUserRegistrationUseCase(
      UserRepository repository,
      CodeVerificationService codeVerificationService,
      EmailService emailService) {
    this.repository = repository;
    this.codeVerificationService = codeVerificationService;
    this.emailService = emailService;
  }

  public StartUserRegistrationResult startUserRegistration(StartUserRegistrationRequest request) {

    Optional<User> user =
        this.repository.resolveLogin(request.email()).flatMap(this.repository::get);

    if (user.isPresent()) {
      return this.handleEmailAlreadyExists(request, user.get());
    }

    UserRegistrationPayload payload = new UserRegistrationPayload(request.email());

    CodeVerificationChallenge challenge = this.codeVerificationService.startChallenge(payload);

    TemplateEmailMessage message =
        this.createSendCodeEmailMessage(request.emailParameters(), challenge);
    this.sendEmail(request, message);

    return new StartUserRegistrationResult(
        challenge.challengeId(), challenge.createdAt(), challenge.expiresAt());
  }

  private StartUserRegistrationResult handleEmailAlreadyExists(
      StartUserRegistrationRequest request, User user) {

    TemplateEmailMessage message =
        this.createSendEmailAlreadyExistsMessage(request.emailParameters(), user);
    this.sendEmail(request, message);

    CodeVerificationChallenge challenge = this.codeVerificationService.createFakeChallenge();

    return new StartUserRegistrationResult(
        challenge.challengeId(), challenge.createdAt(), challenge.expiresAt());
  }

  private void sendEmail(StartUserRegistrationRequest request, TemplateEmailMessage message) {

    Email email =
        Email.builder()
            .from(request.emailParameters().from())
            .replyTo(configurer -> configurer.set(request.emailParameters().replyTo()))
            .to(
                configurer ->
                    configurer.set(EmailAddress.builder().address(request.email()).build()))
            .message(message)
            .build();
    try {
      this.emailService.send(email);
    } catch (EmailSendException e) {
      throw new StartUserRegistrationException("Send email failed", e);
    }
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private TemplateEmailMessage createSendCodeEmailMessage(
      ExternalEmailParameters parameters, CodeVerificationChallenge challenge) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", challenge.code());
    data.put("expiresAt", formatExpiresAt(challenge.expiresAt()));

    return TemplateEmailMessage.builder()
        .messageType(USER_REGISTRATION_SEND_VERIFICATION_CODE)
        .theme(parameters.theme())
        .lang(parameters.lang())
        .data(configurer -> configurer.putAll(data))
        .build();
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private TemplateEmailMessage createSendEmailAlreadyExistsMessage(
      ExternalEmailParameters parameters, User user) {
    Map<String, Object> data = new HashMap<>();
    data.put(
        "user",
        com.sitepark.ies.sharedkernel.security.User.builder()
            .id(user.id())
            .email(user.email())
            .firstName(user.firstName())
            .lastName(user.lastName())
            .username(user.login())
            .build());

    return TemplateEmailMessage.builder()
        .messageType(USER_REGISTRATION_EMAIL_ALREADY_EXISTS)
        .theme(parameters.theme())
        .lang(parameters.lang())
        .data(configurer -> configurer.putAll(data))
        .build();
  }

  private String formatExpiresAt(Instant expiresAt) {
    return expiresAt
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm"));
  }
}
