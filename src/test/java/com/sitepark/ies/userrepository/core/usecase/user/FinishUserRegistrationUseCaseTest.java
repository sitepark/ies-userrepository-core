package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.sharedkernel.email.Email;
import com.sitepark.ies.sharedkernel.email.EmailAddress;
import com.sitepark.ies.sharedkernel.email.EmailMessageThemeIdentifier;
import com.sitepark.ies.sharedkernel.email.EmailSendException;
import com.sitepark.ies.sharedkernel.email.EmailService;
import com.sitepark.ies.sharedkernel.email.ExternalEmailParameters;
import com.sitepark.ies.sharedkernel.security.CodeVerificationChallenge;
import com.sitepark.ies.sharedkernel.security.CodeVerificationFailedException;
import com.sitepark.ies.sharedkernel.security.CodeVerificationPayload;
import com.sitepark.ies.sharedkernel.security.CodeVerificationService;
import com.sitepark.ies.userrepository.core.domain.entity.GenderType;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.exception.FinishUserRegistrationException;
import com.sitepark.ies.userrepository.core.usecase.CreateUser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class FinishUserRegistrationUseCaseTest {

  private static final ExternalEmailParameters EMAIL_PARAMETERS = createTestEmailParameters();

  private CodeVerificationService codeVerificationService;
  private CreateUser createUser;
  private EmailService emailService;

  private FinishUserRegistrationUseCase useCase;

  @BeforeEach
  void setUp() {
    this.codeVerificationService = mock();
    this.createUser = mock();
    this.emailService = mock();

    this.useCase =
        new FinishUserRegistrationUseCase(
            this.codeVerificationService, this.createUser, this.emailService);
  }

  @Test
  void testReturnsResultWithEmailFromPayload() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    FinishUserRegistrationResult result = this.useCase.finishUserRegistration(request);

    assertEquals(
        "test@example.com",
        result.email(),
        "Result should contain the email from the challenge payload");
  }

  @Test
  void testReturnsResultWithUserIdFromCreateUser() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("789");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    FinishUserRegistrationResult result = this.useCase.finishUserRegistration(request);

    assertEquals(
        "789", result.id(), "Result should contain the user ID returned from createUser operation");
  }

  @Test
  void testCallsFinishChallengeWithRequestParameters() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.codeVerificationService).finishChallenge("challenge-123", 123456);
  }

  @Test
  void testCreatesUserWithEmailFromPayload() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(
            argThat(
                user ->
                    user.getLogin().equals("test@example.com")
                        && user.getEmail().isPresent()
                        && user.getEmail().get().equals("test@example.com")));
  }

  @Test
  void testCreatesUserWithFirstNameFromRequest() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Erika",
            "Mustermann",
            GenderType.FEMALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(
            argThat(
                user ->
                    user.getFirstName().isPresent() && user.getFirstName().get().equals("Erika")));
  }

  @Test
  void testCreatesUserWithLastNameFromRequest() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Schmidt",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(
            argThat(
                user ->
                    user.getLastName().isPresent() && user.getLastName().get().equals("Schmidt")));
  }

  @Test
  void testCreatesUserWithGenderFromRequest() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.DIVERSE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(argThat(user -> user.getGender().equals(GenderType.DIVERSE)));
  }

  @Test
  void testCreatesUserWithDefaultGenderWhenNotProvided() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            null,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(argThat(user -> user.getGender().equals(GenderType.UNKNOWN)));
  }

  @Test
  void testCreatesUserWithPasswordFromRequest() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "myPassword123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(
            argThat(
                user ->
                    user.getPassword().isPresent()
                        && user.getPassword().get().getClearText().equals("myPassword123")));
  }

  @Test
  void testCreatesUserWithRoleFromIdIdentifier() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    Identifier roleId = Identifier.ofId("999");
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(roleId),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(
            argThat(
                user ->
                    !user.getRoles().isEmpty()
                        && user.getRoles().getFirst() instanceof Ref
                        && ((Ref) user.getRoles().getFirst()).getId().isPresent()
                        && ((Ref) user.getRoles().getFirst()).getId().get().equals("999")));
  }

  @Test
  void testCreatesUserWithRoleFromAnchorIdentifier() {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    Identifier roleAnchor = Identifier.ofAnchor(Anchor.ofString("admin.role"));
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(roleAnchor),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.createUser)
        .createUser(
            argThat(
                user ->
                    !user.getRoles().isEmpty()
                        && user.getRoles().getFirst() instanceof Ref
                        && ((Ref) user.getRoles().getFirst()).getAnchor().isPresent()
                        && ((Ref) user.getRoles().getFirst())
                            .getAnchor()
                            .get()
                            .getName()
                            .equals("admin.role")));
  }

  @Test
  void testSendsConfirmationEmail() throws EmailSendException {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    this.useCase.finishUserRegistration(request);

    verify(this.emailService).send(any(Email.class));
  }

  @Test
  void testThrowsCodeVerificationFailedExceptionWhenPayloadIsNotUserRegistrationPayload() {

    CodeVerificationPayload wrongPayload = mock(CodeVerificationPayload.class);
    CodeVerificationChallenge challenge =
        new CodeVerificationChallenge(
            "challenge-123",
            123456,
            wrongPayload,
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T11:00:00Z"),
            0);
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    assertThrows(
        CodeVerificationFailedException.class,
        () -> this.useCase.finishUserRegistration(request),
        "Should throw CodeVerificationFailedException when payload type does not match expected"
            + " UserRegistrationPayload");
  }

  @Test
  void testThrowsFinishUserRegistrationExceptionWhenEmailSendFails() throws EmailSendException {

    CodeVerificationChallenge challenge = createTestChallenge("test@example.com");
    when(this.codeVerificationService.finishChallenge("challenge-123", 123456))
        .thenReturn(challenge);

    when(this.createUser.createUser(any(User.class))).thenReturn("456");

    doThrow(new EmailSendException("SMTP connection failed"))
        .when(this.emailService)
        .send(any(Email.class));

    // Using EMAIL_PARAMETERS constant
    FinishUserRegistrationRequest request =
        new FinishUserRegistrationRequest(
            "challenge-123",
            123456,
            "Max",
            "Mustermann",
            GenderType.MALE,
            "secret123",
            List.of(),
            EMAIL_PARAMETERS);

    assertThrows(
        FinishUserRegistrationException.class,
        () -> this.useCase.finishUserRegistration(request),
        "Should throw FinishUserRegistrationException when email sending fails to notify caller of"
            + " the error");
  }

  private static ExternalEmailParameters createTestEmailParameters() {
    EmailAddress from = EmailAddress.builder().address("noreply@example.com").build();
    EmailAddress replyTo = EmailAddress.builder().address("support@example.com").build();
    EmailMessageThemeIdentifier theme = new EmailMessageThemeIdentifier("default", "light");
    return new ExternalEmailParameters(from, List.of(replyTo), theme, "en");
  }

  private CodeVerificationChallenge createTestChallenge(String email) {
    UserRegistrationPayload payload = new UserRegistrationPayload(email);
    return new CodeVerificationChallenge(
        "challenge-123",
        123456,
        payload,
        Instant.parse("2025-01-01T10:00:00Z"),
        Instant.parse("2025-01-01T11:00:00Z"),
        0);
  }
}
