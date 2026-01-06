package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.email.Email;
import com.sitepark.ies.sharedkernel.email.EmailAddress;
import com.sitepark.ies.sharedkernel.email.EmailMessageThemeIdentifier;
import com.sitepark.ies.sharedkernel.email.EmailSendException;
import com.sitepark.ies.sharedkernel.email.EmailService;
import com.sitepark.ies.sharedkernel.email.ExternalEmailParameters;
import com.sitepark.ies.sharedkernel.security.CodeVerificationChallenge;
import com.sitepark.ies.sharedkernel.security.CodeVerificationService;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.StartUserRegistrationException;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class StartUserRegistrationUseCaseTest {

  private static final ExternalEmailParameters EMAIL_PARAMETERS = createTestEmailParameters();

  private UserRepository repository;
  private CodeVerificationService codeVerificationService;
  private EmailService emailService;

  private StartUserRegistrationUseCase useCase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.codeVerificationService = mock();
    this.emailService = mock();

    this.useCase =
        new StartUserRegistrationUseCase(
            this.repository, this.codeVerificationService, this.emailService);
  }

  @Test
  void testReturnsResultWithChallengeIdWhenEmailDoesNotExist() {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    CodeVerificationChallenge challenge =
        createTestChallenge(
            "challenge-123",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    StartUserRegistrationResult result = this.useCase.startUserRegistration(request);

    assertEquals(
        "challenge-123",
        result.challengeId(),
        "Result should contain the challenge ID from the code verification service");
  }

  @Test
  void testReturnsResultWithCreatedAtWhenEmailDoesNotExist() {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");
    CodeVerificationChallenge challenge =
        createTestChallenge("challenge-123", createdAt, Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    StartUserRegistrationResult result = this.useCase.startUserRegistration(request);

    assertEquals(
        createdAt,
        result.createdAt(),
        "Result should contain the creation timestamp from the code verification service");
  }

  @Test
  void testReturnsResultWithExpiresAtWhenEmailDoesNotExist() {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    Instant expiresAt = Instant.parse("2025-01-01T11:00:00Z");
    CodeVerificationChallenge challenge =
        createTestChallenge("challenge-123", Instant.parse("2025-01-01T10:00:00Z"), expiresAt);
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    StartUserRegistrationResult result = this.useCase.startUserRegistration(request);

    assertEquals(
        expiresAt,
        result.expiresAt(),
        "Result should contain the expiration timestamp from the code verification service");
  }

  @Test
  void testCallsStartChallengeWithCorrectPayloadWhenEmailDoesNotExist() {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    CodeVerificationChallenge challenge =
        createTestChallenge(
            "challenge-123",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    this.useCase.startUserRegistration(request);

    verify(this.codeVerificationService)
        .startChallenge(
            argThat(
                payload ->
                    payload instanceof UserRegistrationPayload
                        && "test@example.com".equals(((UserRegistrationPayload) payload).email())));
  }

  @Test
  void testSendsVerificationCodeEmailWhenEmailDoesNotExist() throws EmailSendException {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    CodeVerificationChallenge challenge =
        createTestChallenge(
            "challenge-123",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    this.useCase.startUserRegistration(request);

    verify(this.emailService).send(any(Email.class));
  }

  @Test
  void testReturnsResultWithFakeChallengeIdWhenEmailAlreadyExists() {

    when(this.repository.resolveLogin("test@example.com")).thenReturn(Optional.of("123"));
    User existingUser =
        User.builder()
            .id("123")
            .login("test@example.com")
            .email("test@example.com")
            .firstName("Max")
            .lastName("Mustermann")
            .build();
    when(this.repository.get("123")).thenReturn(Optional.of(existingUser));

    CodeVerificationChallenge fakeChallenge =
        createTestChallenge(
            "fake-challenge-456",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.createFakeChallenge()).thenReturn(fakeChallenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    StartUserRegistrationResult result = this.useCase.startUserRegistration(request);

    assertEquals(
        "fake-challenge-456",
        result.challengeId(),
        "Result should contain the fake challenge ID when email already exists to prevent user"
            + " enumeration");
  }

  @Test
  void testReturnsResultWithFakeChallengeCreatedAtWhenEmailAlreadyExists() {

    when(this.repository.resolveLogin("test@example.com")).thenReturn(Optional.of("123"));
    User existingUser =
        User.builder()
            .id("123")
            .login("test@example.com")
            .email("test@example.com")
            .firstName("Max")
            .lastName("Mustermann")
            .build();
    when(this.repository.get("123")).thenReturn(Optional.of(existingUser));

    Instant fakeCreatedAt = Instant.parse("2025-01-01T10:00:00Z");
    CodeVerificationChallenge fakeChallenge =
        createTestChallenge(
            "fake-challenge-456", fakeCreatedAt, Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.createFakeChallenge()).thenReturn(fakeChallenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    StartUserRegistrationResult result = this.useCase.startUserRegistration(request);

    assertEquals(
        fakeCreatedAt,
        result.createdAt(),
        "Result should contain the fake creation timestamp when email already exists to prevent"
            + " user enumeration");
  }

  @Test
  void testReturnsResultWithFakeChallengeExpiresAtWhenEmailAlreadyExists() {

    when(this.repository.resolveLogin("test@example.com")).thenReturn(Optional.of("123"));
    User existingUser =
        User.builder()
            .id("123")
            .login("test@example.com")
            .email("test@example.com")
            .firstName("Max")
            .lastName("Mustermann")
            .build();
    when(this.repository.get("123")).thenReturn(Optional.of(existingUser));

    Instant fakeExpiresAt = Instant.parse("2025-01-01T11:00:00Z");
    CodeVerificationChallenge fakeChallenge =
        createTestChallenge(
            "fake-challenge-456", Instant.parse("2025-01-01T10:00:00Z"), fakeExpiresAt);
    when(this.codeVerificationService.createFakeChallenge()).thenReturn(fakeChallenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    StartUserRegistrationResult result = this.useCase.startUserRegistration(request);

    assertEquals(
        fakeExpiresAt,
        result.expiresAt(),
        "Result should contain the fake expiration timestamp when email already exists to prevent"
            + " user enumeration");
  }

  @Test
  void testCallsCreateFakeChallengeWhenEmailAlreadyExists() {

    when(this.repository.resolveLogin("test@example.com")).thenReturn(Optional.of("123"));
    User existingUser =
        User.builder()
            .id("123")
            .login("test@example.com")
            .email("test@example.com")
            .firstName("Max")
            .lastName("Mustermann")
            .build();
    when(this.repository.get("123")).thenReturn(Optional.of(existingUser));

    CodeVerificationChallenge fakeChallenge =
        createTestChallenge(
            "fake-challenge-456",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.createFakeChallenge()).thenReturn(fakeChallenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    this.useCase.startUserRegistration(request);

    verify(this.codeVerificationService).createFakeChallenge();
  }

  @Test
  void testSendsEmailAlreadyExistsNotificationWhenEmailAlreadyExists() throws EmailSendException {

    when(this.repository.resolveLogin("test@example.com")).thenReturn(Optional.of("123"));
    User existingUser =
        User.builder()
            .id("123")
            .login("test@example.com")
            .email("test@example.com")
            .firstName("Max")
            .lastName("Mustermann")
            .build();
    when(this.repository.get("123")).thenReturn(Optional.of(existingUser));

    CodeVerificationChallenge fakeChallenge =
        createTestChallenge(
            "fake-challenge-456",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.createFakeChallenge()).thenReturn(fakeChallenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    this.useCase.startUserRegistration(request);

    verify(this.emailService).send(any(Email.class));
  }

  @Test
  void testThrowsStartUserRegistrationExceptionWhenEmailSendFails() throws EmailSendException {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    CodeVerificationChallenge challenge =
        createTestChallenge(
            "challenge-123",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    doThrow(new EmailSendException("SMTP server unavailable"))
        .when(this.emailService)
        .send(any(Email.class));

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    assertThrows(
        StartUserRegistrationException.class,
        () -> this.useCase.startUserRegistration(request),
        "Should throw StartUserRegistrationException when email sending fails to alert caller of"
            + " the error");
  }

  @Test
  void testResolvesLoginWithRequestedEmailAddress() {

    when(this.repository.resolveLogin(anyString())).thenReturn(Optional.empty());

    CodeVerificationChallenge challenge =
        createTestChallenge(
            "challenge-123",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.startChallenge(any(UserRegistrationPayload.class)))
        .thenReturn(challenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    this.useCase.startUserRegistration(request);

    verify(this.repository).resolveLogin("test@example.com");
  }

  @Test
  void testRetrievesExistingUserWhenLoginIsResolved() {

    when(this.repository.resolveLogin("test@example.com")).thenReturn(Optional.of("123"));
    User existingUser =
        User.builder()
            .id("123")
            .login("test@example.com")
            .email("test@example.com")
            .firstName("Max")
            .lastName("Mustermann")
            .build();
    when(this.repository.get("123")).thenReturn(Optional.of(existingUser));

    CodeVerificationChallenge fakeChallenge =
        createTestChallenge(
            "fake-challenge-456",
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"));
    when(this.codeVerificationService.createFakeChallenge()).thenReturn(fakeChallenge);

    StartUserRegistrationRequest request =
        new StartUserRegistrationRequest("test@example.com", EMAIL_PARAMETERS);

    this.useCase.startUserRegistration(request);

    verify(this.repository).get("123");
  }

  private static ExternalEmailParameters createTestEmailParameters() {
    EmailAddress from = EmailAddress.builder().address("noreply@example.com").build();
    EmailAddress replyTo = EmailAddress.builder().address("support@example.com").build();
    EmailMessageThemeIdentifier theme = new EmailMessageThemeIdentifier("default", "light");
    return new ExternalEmailParameters(from, List.of(replyTo), theme, "en");
  }

  private CodeVerificationChallenge createTestChallenge(
      String challengeId, Instant createdAt, Instant expiresAt) {
    UserRegistrationPayload payload = new UserRegistrationPayload("test@example.com");
    return new CodeVerificationChallenge(challengeId, 123456, payload, createdAt, expiresAt, 0);
  }
}
